package me.j0keer.xpillars.data.type;

import com.sun.tools.jconsole.JConsoleContext;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.data.Database;
import me.j0keer.xpillars.player.GamePlayer;
import me.j0keer.xpillars.player.PlayerData;

import java.sql.*;
import java.util.HashMap;

public class SQLData extends Database {
    private Connection connection;
    private final String TABLE_DATA = "player_data";

    public SQLData(XPillars plugin) {
        super(plugin);
    }

    @Override
    protected String getType() {
        return "SQL";
    }

    @Override
    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/database.db");
            plugin.console("{prefix}&7Connected to database.");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Statement var1 = null;
        PlayerData data = new PlayerData();
        HashMap<String, PlayerData.Data> schema = data.getSchema();

        try {
            var1 = connection.createStatement();
            StringBuilder creating = new StringBuilder();
            schema.forEach((key, value) -> {
                var type = value.getType();
                var defaultValue = value.getDefaultValue();
                if (type == String.class) {
                    creating.append(", '").append(key).append("' VARCHAR(255)");
                } else if (type == Integer.class) {
                    creating.append(", '").append(key).append("' INTEGER DEFAULT ").append(defaultValue);
                } else if (type == Boolean.class) {
                    creating.append(", '").append(key).append("' BOOLEAN DEFAULT ").append(defaultValue);
                } else if (type == Double.class) {
                    creating.append(", '").append(key).append("' DOUBLE DEFAULT ").append(defaultValue);
                } else if (type == Long.class) {
                    creating.append(", '").append(key).append("' BIGINT DEFAULT ").append(defaultValue);
                } else {
                    plugin.console("{prefix}&cUnknown type: " + type);
                }
            });
            creating.delete(0, 2);
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s (uuid VARCHAR(36) NOT NULL UNIQUE, %s);", TABLE_DATA, creating);
            getPlugin().debug(sql);
            var1.executeUpdate(sql);
            addColumn("uuid", "VARCHAR(36) NOT NULL UNIQUE");

            schema.forEach((key, value) -> {
                var type = value.getType();
                if (type == String.class) {
                    addColumn(key, "VARCHAR(255)");
                } else if (type == Integer.class) {
                    addColumn(key, "INTEGER");
                } else if (type == Boolean.class) {
                    addColumn(key, "BOOLEAN");
                } else if (type == Double.class) {
                    addColumn(key, "DOUBLE");
                } else if (type == Long.class) {
                    addColumn(key, "BIGINT");
                } else {
                    plugin.console("{prefix}&cUnknown type: " + type);
                }
            });
            var1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(var1);
        }

        // Get all data in table
        try {
            var1 = connection.createStatement();
            ResultSet var2 = var1.executeQuery("SELECT * FROM " + TABLE_DATA);
            while (var2.next()) {
                plugin.debug("uuid = " + var2.getString("uuid"));
                schema.forEach((key, value) -> {
                    try {
                        var type = value.getType();
                        if (type == String.class) {
                            plugin.debug(key + " = " + var2.getString(key));
                        } else if (type == Integer.class) {
                            plugin.debug(key + " = " + var2.getInt(key));
                        } else if (type == Boolean.class) {
                            plugin.debug(key + " = " + var2.getBoolean(key));
                        } else if (type == Double.class) {
                            plugin.debug(key + " = " + var2.getDouble(key));
                        } else if (type == Long.class) {
                            plugin.debug(key + " = " + var2.getLong(key));
                        } else {
                            plugin.console("{prefix}&cUnknown type: " + type);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
            var2.close();
            var1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(var1);
        }
    }

    private void addColumn(String var2, String var3) {
        ResultSet var4 = null;
        Statement var5 = null;

        try {
            var5 = connection.createStatement();
            DatabaseMetaData var6 = connection.getMetaData();
            var4 = var6.getColumns(null, null, TABLE_DATA, var2);
            if (!var4.next()) {
                var5.executeUpdate(String.format("ALTER TABLE %s ADD COLUMN %s %s;", TABLE_DATA, var2, var3));
            }
        } catch (SQLException var10) {
            var10.printStackTrace();
        } finally {
            this.close(var4);
            this.close(var5);
        }
    }

    @Override
    public void disconnect() {
        close(connection);
        getPlugin().console("{prefix}&7Disconnected from database.");
    }

    public void close(AutoCloseable var1) {
        if (var1 != null) {
            try {
                var1.close();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void loadUser(GamePlayer user) {
        PreparedStatement var1 = null;
        ResultSet var2 = null;

        HashMap<String, PlayerData.Data> schema = user.getSchema();

        try {
            var1 = connection.prepareStatement(String.format("SELECT * FROM %s WHERE uuid = ?;", TABLE_DATA));
            var1.setString(1, user.getUuid().toString());
            var2 = var1.executeQuery();
            if (var2.next()) {
                ResultSet finalVar = var2;
                schema.forEach((key, value) -> {
                    try {
                        var type = value.getType();
                        if (type == String.class) {
                            value.set(finalVar.getString(key));
                        } else if (type == Integer.class) {
                            value.set(finalVar.getInt(key));
                        } else if (type == Boolean.class) {
                            value.set(finalVar.getBoolean(key));
                        } else if (type == Double.class) {
                            value.set(finalVar.getDouble(key));
                        } else if (type == Long.class) {
                            value.set(finalVar.getLong(key));
                        } else {
                            plugin.console("{prefix}&cUnknown type: " + type);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                plugin.debug("Loaded user " + user.getName() + " from database.");
            } else {
                user.setSaved(false);
                plugin.debug("User " + user.getUuid() + " not found in database. New entry will created save.");
                createUser(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(var2);
            this.close(var1);
        }
    }

    public void createUser(GamePlayer user) {
        PreparedStatement var1 = null;

        HashMap<String, PlayerData.Data> schema = user.getSchema();

        try {
            StringBuilder columns = new StringBuilder("uuid, ");
            StringBuilder values = new StringBuilder("?, ");
            schema.forEach((key, value) -> {
                columns.append(key).append(", ");
                values.append("?, ");
            });
            columns.delete(columns.length() - 2, columns.length());
            values.delete(values.length() - 2, values.length());
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);", TABLE_DATA, columns, values);
            getPlugin().debug(sql);
            var1 = connection.prepareStatement(sql);
            var ref = new Object() {
                int indexOut = 1;
            };


            PreparedStatement finalVar = var1;
            finalVar.setString(ref.indexOut++, user.getUuid().toString());

            schema.forEach((key, values2) -> {
                int indexIn = ref.indexOut;
                try {
                    var type = values2.getType();
                    var value = values2.getActualValue();
                    if (type == String.class) {
                        finalVar.setString(indexIn, (String) value);
                    } else if (type == Integer.class) {
                        finalVar.setInt(indexIn, (Integer) value);
                    } else if (type == Boolean.class) {
                        finalVar.setBoolean(indexIn, (Boolean) value);
                    } else if (type == Double.class) {
                        finalVar.setDouble(indexIn, (Double) value);
                    } else if (type == Long.class) {
                        finalVar.setLong(indexIn, (Long) value);
                    } else {
                        plugin.console("{prefix}&cUnknown type: " + type);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                getPlugin().debug("Set value " + key + " with index " + ref.indexOut);
                ref.indexOut++;
            });

            var1.executeUpdate();
            plugin.debug("Created user " + user.getName() + " in database.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(var1);
        }
    }

    @Override
    public void saveUser(GamePlayer user) {
        PreparedStatement var1 = null;

        HashMap<String, PlayerData.Data> schema = user.getSchema();

        try {
            StringBuilder columns = new StringBuilder();
            schema.forEach((key, value) -> columns.append(key).append(" = ?, "));
            columns.delete(columns.length() - 2, columns.length());
            String sql = String.format("UPDATE %s SET %s WHERE uuid = ?;", TABLE_DATA, columns);
            getPlugin().debug(sql);
            var1 = connection.prepareStatement(sql);
            var ref = new Object() {
                int indexOut = 1;
            };

            PreparedStatement finalVar = var1;
            schema.forEach((key, values) -> {
                int indexIn = ref.indexOut;
                try {
                    var type = values.getType();
                    var value = values.getActualValue();
                    if (type == String.class) {
                        finalVar.setString(indexIn, (String) value);
                    } else if (type == Integer.class) {
                        finalVar.setInt(indexIn, (Integer) value);
                    } else if (type == Boolean.class) {
                        finalVar.setBoolean(indexIn, (Boolean) value);
                    } else if (type == Double.class) {
                        finalVar.setDouble(indexIn, (Double) value);
                    } else if (type == Long.class) {
                        finalVar.setLong(indexIn, (Long) value);
                    } else {
                        plugin.console("{prefix}&cUnknown type: " + type);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                getPlugin().debug("Set value " + key + " with index " + ref.indexOut);
                ref.indexOut++;
            });

            getPlugin().debug("Set uuid with index " + ref.indexOut);
            var1.setString(ref.indexOut, user.getUuid().toString());
            var1.executeUpdate();
            plugin.debug("Saved user " + user.getName() + " to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(var1);
        }
    }
}
