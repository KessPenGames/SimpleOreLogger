package net.simple.orelogger.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class OreLogsDatabase {
    private final DataSource ds;
    private final String table;

    public OreLogsDatabase(DataSource ds, String table) {
        this.ds = ds;
        this.table = table;
        init();
    }

    private void init() {
        CompletableFuture.runAsync(() -> {
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = ds.getConnection();
                stmt = conn.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS `"+table+"` (`player` VARCHAR(48) NOT NULL, " +
                                "`type` VARCHAR(48), `x` INT, `y` INT, `z` INT, `date` TIMESTAMP);"
                );
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void createStatistic(UUID uuid, String type, Location loc, String date) {
        CompletableFuture.runAsync(() -> {
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = ds.getConnection();
                stmt = conn.prepareStatement(
                        "INSERT INTO `"+table+"` (`player`, `type`, `x`, `y`, `z`, `date`) " +
                                "VALUES ('"+uuid.toString()+"', '"+type+"', '"+
                                loc.getBlockX()+"', '"+loc.getBlockY()+"', '"+loc.getBlockZ()+"', '"+date+"');"
                );
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateStatistic(UUID uuid, String type) {
        CompletableFuture.runAsync(() -> {
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = ds.getConnection();
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance()
                        .getTime());
                stmt = conn.prepareStatement("UPDATE `"+table+"` SET `type` = '"+type+"', " +
                        "`date` = '"+timeStamp+"' WHERE `"+table+"`.`player` = '"+uuid.toString()+"'");
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public CompletableFuture<List<OreLogsTable>> getAllStatistics(UUID uuid, int days) {
        return CompletableFuture.supplyAsync(() -> {
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                Date date = new Date();
                date.setTime(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days - 1));
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                conn = ds.getConnection();
                stmt = conn.prepareStatement(
                        "SELECT * FROM `"+table+"` WHERE `player` " +
                                "= '"+uuid.toString()+"' and `date` > '"+timeStamp+"'"
                );
                try (ResultSet rs = stmt.executeQuery()) {
                    List<OreLogsTable> list = new ArrayList<>();
                    while (rs.next()) {
                        list.add(new OreLogsTable(
                                uuid,
                                rs.getString("type"),
                                new Location(
                                        Bukkit.getWorld("world"),
                                        rs.getInt("x"),
                                        rs.getInt("y"),
                                        rs.getInt("z")
                                ),
                                rs.getTimestamp("date")
                        ));
                    }
                    return list;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public CompletableFuture<List<OreLogsTable>> getLastStatistics(UUID uuid, int limit, String type) {
        return CompletableFuture.supplyAsync(() -> {
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                String where_type = type != null ? "and `type` = '" + type + "'" : "";
                conn = ds.getConnection();
                stmt = conn.prepareStatement(
                        "SELECT * FROM `"+table+"` WHERE `player` " +
                                "= '"+uuid.toString()+"' "+where_type+" order by `date` DESC limit " + limit
                );
                try (ResultSet rs = stmt.executeQuery()) {
                    List<OreLogsTable> list = new ArrayList<>();
                    while (rs.next()) {
                        list.add(new OreLogsTable(
                                uuid,
                                rs.getString("type"),
                                new Location(
                                        Bukkit.getWorld("world"),
                                        rs.getInt("x"),
                                        rs.getInt("y"),
                                        rs.getInt("z")
                                ),
                                rs.getTimestamp("date")
                        ));
                    }
                    return list;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public CompletableFuture<Map<String, Integer>> getTop(int count, String type, int days) {
        return CompletableFuture.supplyAsync(() -> {
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                Date date = new Date();
                date.setTime(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days - 1));
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                String where_date = days != 0 ? "and `date` > '"+timeStamp+"'" : "";
                conn = ds.getConnection();
                stmt = conn.prepareStatement(
                        "SELECT player, type, COUNT(*) as count FROM `"+table+"` WHERE type='"+type+"' "
                                + where_date + " GROUP BY player, type HAVING COUNT(*) > "+count+" ORDER BY" +
                                " COUNT DESC;"
                );
                try (ResultSet rs = stmt.executeQuery()) {
                    Map<String, Integer> map = new HashMap<>();
                    while (rs.next()) {
                        map.put(rs.getString("player"), rs.getInt("count"));
                    }
                    return map;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public CompletableFuture<Boolean> contains(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = ds.getConnection();
                stmt = conn.prepareStatement(
                        "SELECT * FROM `"+table+"` WHERE `player` LIKE '%"+uuid.toString()+"%'"
                );
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
