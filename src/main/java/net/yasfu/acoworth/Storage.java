package net.yasfu.acoworth;

import java.sql.*;
import java.util.ArrayList;
import org.bukkit.Material;

public class Storage {

    public static Connection conn;

    public static void connect() {
        try {
            String url = "jdbc:sqlite:plugins/AcoWorth/acoworth.db";
            conn = DriverManager.getConnection(url);

            checkTables();
        } catch (SQLException e) {
            AcoWorthPlugin.singleton.getLogger().severe(e.getMessage());
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
        try {
            Statement st = conn.createStatement();
            st.setQueryTimeout(30);

            st.executeUpdate("CREATE TABLE IF NOT EXISTS " +
                    "sales (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "mc_material VARCHAR(255) NOT NULL, " +
                    "sale_amt DOUBLE NOT NULL)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS " +
                    "worth (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "mc_material VARCHAR(255) NOT NULL UNIQUE, " +
                    "lastWorth DOUBLE NOT NULL, " +
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

            st.executeUpdate("INSERT INTO sales (mc_material, sale_amt) VALUES ('" + matName + "', '" + pricePer + "')");
            st.executeUpdate("DELETE FROM sales WHERE id NOT IN " +
                    "(SELECT id FROM sales WHERE mc_material = '" + matName + "' ORDER BY id DESC LIMIT " + fileCap + ") " +
                    "AND mc_material = '" + matName + "'");
            st.executeUpdate("UPDATE worth SET needsUpdate = 1 WHERE mc_material = '" + matName + "'");
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
            ResultSet rs = st.executeQuery("SELECT * FROM sales WHERE mc_material = '" + matName + "'");

            ArrayList<Double> values = new ArrayList<Double>();

            while (rs.next()) {
                values.add(rs.getDouble("sale_amt"));
            }

            st = conn.createStatement();
            ResultSet rsWorthCache = st.executeQuery("SELECT * FROM worth WHERE mc_material = '" + matName + "'");

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

            double multiplier = AcoWorthPlugin.singleton.getConfig().getDouble("standardDeviationMuiltiplier");

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

            st = conn.createStatement();
            st.executeUpdate("INSERT OR REPLACE INTO worth (mc_material, lastWorth, needsUpdate) VALUES ('" + matName + "', " + worth + ", 0)");

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
