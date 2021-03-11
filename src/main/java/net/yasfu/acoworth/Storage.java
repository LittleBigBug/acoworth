package net.yasfu.acoworth;

import java.sql.*;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import static org.bukkit.Bukkit.getServer;

public class Storage {

    public static Connection conn;
    public static String tablePrefix = "";

    public static void connect() {
        try {
            FileConfiguration cfg = AcoWorthPlugin.singleton.getConfig();
            boolean mysql = cfg.getBoolean("storage.useMySQL");
            boolean pgsql = cfg.getBoolean("storage.usePostgreSQL");

            String url = "jdbc:sqlite:plugins/AcoWorth/acoworth.db";

            String user = "";
            String pass = "";

            if (pgsql || mysql) {
                String address = cfg.getString("storage.credentials.address");
                String db = cfg.getString("storage.credentials.database");

                user = cfg.getString("storage.credentials.username");
                pass = cfg.getString("storage.credentials.password");

                tablePrefix = cfg.getString("storage.tablePrefix");

                if (mysql) {
                    url = "jdbc:mysql://" + address + "/" + db;
                } else {
                    url = "jdbc:postgresql://" + address + "/" + db;
                }
            }

            conn = DriverManager.getConnection(url, user, pass);

            checkTables();
        } catch (SQLException e) {
            AcoWorthPlugin.singleton.getLogger().severe(e.getMessage());
            getServer().getPluginManager().disablePlugin(AcoWorthPlugin.singleton);
        }
    }

    public static void disconnect() {
        try {
            conn.close();
        } catch (SQLException | NullPointerException e) {
            AcoWorthPlugin.singleton.getLogger().severe(e.getMessage());
        }
    }

    public static void checkTables() {
        FileConfiguration cfg = AcoWorthPlugin.singleton.getConfig();
        tablePrefix = cfg.getString("storage.tablePrefix");

        try {
            Statement st = conn.createStatement();
            st.setQueryTimeout(30);

            boolean mysql = cfg.getBoolean("storage.useMySQL");
            boolean pgsql = cfg.getBoolean("storage.usePostgreSQL");

            String autoInc = ""; // SQLite doesn't need auto_increment (primary key tells em that)
            String idType = "INTEGER"; // PostgreSQL uses a different type for auto increment
            String dblType = ""; // PostgreSQL thing

            if (mysql) {
                autoInc = " AUTO_INCREMENT";
            } else if (pgsql) {
                idType = "SERIAL";
                dblType = " PRECISION";
            }

            st.executeUpdate("CREATE TABLE IF NOT EXISTS " +
                    tablePrefix + "sales (" +
                    "id " + idType + " PRIMARY KEY" + autoInc + ", " +
                    "mc_material VARCHAR(150) NOT NULL, " +
                    "sale_amt DOUBLE" + dblType + " NOT NULL)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS " +
                    tablePrefix + "worth (" +
                    "id " + idType + " PRIMARY KEY " + autoInc + ", " +
                    "mc_material VARCHAR(150) NOT NULL UNIQUE, " +
                    "lastWorth DOUBLE" + dblType + " NOT NULL, " +
                    "needsUpdate TINYINT(1) NOT NULL DEFAULT 0)");
        } catch (SQLException e) {
            AcoWorthPlugin.singleton.getLogger().severe(e.getMessage());
        }
    }

    public static void addSale(Material mat, double pricePer) {
        try {
            Statement st = conn.createStatement();
            st.setQueryTimeout(30);

            int fileCap = AcoWorthPlugin.singleton.getConfig().getInt("maxPerItem");

            String matName = mat.toString();

            st.executeUpdate("INSERT INTO " + tablePrefix + "sales (mc_material, sale_amt) VALUES ('" + matName + "', '" + pricePer + "')");

            ResultSet ids =  st.executeQuery("SELECT id FROM " + tablePrefix + "sales WHERE mc_material = '" + matName + "' ORDER BY id DESC LIMIT " + fileCap);
            StringBuilder csvIDs = new StringBuilder(); // Hacky solution for older MySQL / MariaDB servers

            while (ids.next()) {
                int id = ids.getInt("id");

                if (!ids.isFirst()) {
                    csvIDs.append(",");
                }

                csvIDs.append(id);
            }

            st.executeUpdate("DELETE FROM " + tablePrefix + "sales WHERE id NOT IN (" + csvIDs + ") AND mc_material = '" + matName + "'");
            st.executeUpdate("UPDATE " + tablePrefix + "worth SET needsUpdate = 1 WHERE mc_material = '" + matName + "'");
        } catch (SQLException e) {
            AcoWorthPlugin.singleton.getLogger().severe(e.getMessage());
        }
    }

    public static double getWorth(Material mat) {
        double worth = 0;

        try {
            Statement st = conn.createStatement();
            st.setQueryTimeout(30);

            String matName = mat.toString();
            ResultSet rs = st.executeQuery("SELECT * FROM " + tablePrefix + "sales WHERE mc_material = '" + matName + "'");

            ArrayList<Double> values = new ArrayList<Double>();

            while (rs.next()) {
                values.add(rs.getDouble("sale_amt"));
            }

            st = conn.createStatement();
            ResultSet rsWorthCache = st.executeQuery("SELECT * FROM " + tablePrefix + "worth WHERE mc_material = '" + matName + "'");

            double lastWorth = 0;
            boolean needsUpdate = true;

            if (rsWorthCache.next()) {
                lastWorth = rsWorthCache.getDouble("lastWorth");
                needsUpdate = rsWorthCache.getBoolean("needsUpdate");
            }

            if (!needsUpdate) {
                return lastWorth;
            }

            long timeStart = System.currentTimeMillis();

            if (values.size() < 1) {
                return -1d;
            }

            double avg = 0;

            for (double cost : values) {
                avg += cost;
            }

            avg /= values.size();

            // Standard Deviation

            ArrayList<Double> st3Values = new ArrayList<>();

            for (double cost : values) {
                double res = cost - avg;
                res = Math.pow(res, 2);
                st3Values.add(res);
            }

            double avgSt3 = 0;

            for (double val : st3Values) {
                avgSt3 += val;
            }

            avgSt3 /= st3Values.size();
            avgSt3 = Math.sqrt(avgSt3);

            FileConfiguration cfg = AcoWorthPlugin.singleton.getConfig();
            double multiplier = cfg.getDouble("standardDeviationMuiltiplier");

            avgSt3 *= multiplier;

            double finalAvg = 0;
            int used = 0;

            for (double price : values) {
                double min = avg - avgSt3;
                double max = avg + avgSt3;

                if ((price >= min) && (price <= max)) {
                    used++;
                    finalAvg += price;
                }
            }

            if (used < 1) {
                return -1d;
            }

            finalAvg /= used;

            worth = finalAvg;
            boolean mysql = cfg.getBoolean("storage.useMySQL");
            boolean pgsql = cfg.getBoolean("storage.usePostgreSQL");

            st = conn.createStatement();

            if (mysql) {
                st.executeUpdate("INSERT INTO " + tablePrefix + "worth (mc_material, lastWorth, needsUpdate) VALUES ('" + matName + "', " + worth + ", 0) ON DUPLICATE KEY UPDATE lastworth = '" + worth + "', needsUpdate = 0");
            } else if (pgsql) {
                st.executeUpdate("INSERT INTO " + tablePrefix + "worth (mc_material, lastWorth, needsUpdate) VALUES ('" + matName + "', " + worth + ", 0) ON CONFLICT(mc_material) DO UPDATE SET lastworth = '" + worth + "', needsUpdate = 0");
            } else {
                st.executeUpdate("INSERT OR REPLACE INTO " + tablePrefix + "worth (mc_material, lastWorth, needsUpdate) VALUES ('" + matName + "', " + worth + ", 0)");
            }

            long timeEnd = System.currentTimeMillis();
            long diff = timeEnd - timeStart;

            AcoWorthPlugin.singleton.getLogger().info("Calculated new worth for '" + mat.name() + "' took " + diff + "ms");
        } catch (SQLException e) {
            AcoWorthPlugin.singleton.getLogger().severe(e.getMessage());
            return -2d;
        }

        return worth;
    }

}
