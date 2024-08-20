package me.j0keer.xpillars.data.type;

import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.data.Database;
import me.j0keer.xpillars.player.GamePlayer;
import me.j0keer.xpillars.player.PlayerData;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.util.HashMap;

public class MariaDBData extends Database {
    private Connection connection;
    private final String TABLE_DATA = "player_data";

    public MariaDBData(XPillars plugin) {
        super(plugin);
    }

    @Override
    protected String getType() {
        return "MariaDB";
    }

    @Override
    public void connect() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("database.mysql");
        String host;
        int port;
        if (section.getString("host").contains(":")) {
            String[] hostA = section.getString("host").split(":");
            host = hostA[0];
            port = Integer.parseInt(hostA[1]);
        } else {
            host = section.getString("host");
            port = 3306;
        }
        String database = section.getString("database");
        String username = section.getString("user");
        String password = section.getString("password");

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://" + host + ":" + port + "/" + database, username, password);
            plugin.console("{prefix}&7Connected to MariaDB.");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Statement statement = null;
        PlayerData data = new PlayerData();
        HashMap<String, PlayerData.Data> schema = data.getSchema();

        try {
            statement = connection.createStatement();
            StringBuilder creating = new StringBuilder();
            schema.forEach((key, value) -> {
                var type = value.getType();
                var defaultValue = value.getDefaultValue();
                if (type == String.class) {
                    creating.append(", `").append(key).append("` VARCHAR(255)");
                } else if (type == Integer.class) {
                    creating.append(", `").append(key).append("` INT DEFAULT ").append(defaultValue);
                } else if (type == Boolean.class) {
                    creating.append(", `").append(key).append("` BOOLEAN DEFAULT ").append(defaultValue);
                } else if (type == Double.class) {
                    creating.append(", `").append(key).append("` DOUBLE DEFAULT ").append(defaultValue);
                } else if (type == Long.class) {
                    creating.append(", `").append(key).append("` BIGINT DEFAULT ").append(defaultValue);
                } else {
                    plugin.console("{prefix}&cUnknown type: " + type);
                }
            });
            creating.delete(0, 2);
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s (`uuid` VARCHAR(36) NOT NULL UNIQUE, %s);", TABLE_DATA, creating);
            getPlugin().debug(sql);
            statement.executeUpdate(sql);
            addColumn("uuid", "VARCHAR(36) NOT NULL UNIQUE");

            schema.forEach((key, value) -> {
                var type = value.getType();
                if (type == String.class) {
                    addColumn(key, "VARCHAR(255)");
                } else if (type == Integer.class) {
                    addColumn(key, "INT");
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
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
        }

        // Get all data in table
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + TABLE_DATA);
            while (resultSet.next()) {
                plugin.debug("uuid = " + resultSet.getString("uuid"));
                schema.forEach((key, value) -> {
                    try {
                        var type = value.getType();
                        if (type == String.class) {
                            plugin.debug(key + " = " + resultSet.getString(key));
                        } else if (type == Integer.class) {
                            plugin.debug(key + " = " + resultSet.getInt(key));
                        } else if (type == Boolean.class) {
                            plugin.debug(key + " = " + resultSet.getBoolean(key));
                        } else if (type == Double.class) {
                            plugin.debug(key + " = " + resultSet.getDouble(key));
                        } else if (type == Long.class) {
                            plugin.debug(key + " = " + resultSet.getLong(key));
                        } else {
                            plugin.console("{prefix}&cUnknown type: " + type);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
        }
    }

    private void addColumn(String columnName, String columnDefinition) {
        ResultSet resultSet = null;
        Statement statement = null;

        try {
            statement = connection.createStatement();
            DatabaseMetaData metaData = connection.getMetaData();
            resultSet = metaData.getColumns(null, null, TABLE_DATA, columnName);
            if (!resultSet.next()) {
                statement.executeUpdate(String.format("ALTER TABLE %s ADD COLUMN %s %s;", TABLE_DATA, columnName, columnDefinition));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(resultSet);
            this.close(statement);
        }
    }

    @Override
    public void disconnect() {
        close(connection);
        getPlugin().console("{prefix}&7Disconnected from MariaDB.");
    }

    public void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void loadUser(GamePlayer user) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        HashMap<String, PlayerData.Data> schema = user.getSchema();

        try {
            statement = connection.prepareStatement(String.format("SELECT * FROM %s WHERE uuid = ?;", TABLE_DATA));
            statement.setString(1, user.getUuid().toString());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                ResultSet finalResultSet = resultSet;
                schema.forEach((key, value) -> {
                    try {
                        var type = value.getType();
                        if (type == String.class) {
                            value.set(finalResultSet.getString(key));
                        } else if (type == Integer.class) {
                            value.set(finalResultSet.getInt(key));
                        } else if (type == Boolean.class) {
                            value.set(finalResultSet.getBoolean(key));
                        } else if (type == Double.class) {
                            value.set(finalResultSet.getDouble(key));
                        } else if (type == Long.class) {
                            value.set(finalResultSet.getLong(key));
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
                plugin.debug("User " + user.getUuid() + " not found in database. New entry will be created upon save.");
                createUser(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(resultSet);
            this.close(statement);
        }
    }

    public void createUser(GamePlayer user) {
        PreparedStatement statement = null;

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
            statement = connection.prepareStatement(sql);
            var ref = new Object() {
                int indexOut = 1;
            };

            statement.setString(ref.indexOut++, user.getUuid().toString());

            PreparedStatement finalStatement = statement;
            schema.forEach((key, value) -> {
                int indexIn = ref.indexOut;
                try {
                    var type = value.getType();
                    var actualValue = value.getActualValue();
                    if (type == String.class) {
                        finalStatement.setString(indexIn, (String) actualValue);
                    } else if (type == Integer.class) {
                        finalStatement.setInt(indexIn, (Integer) actualValue);
                    } else if (type == Boolean.class) {
                        finalStatement.setBoolean(indexIn, (Boolean) actualValue);
                    } else if (type == Double.class) {
                        finalStatement.setDouble(indexIn, (Double) actualValue);
                    } else if (type == Long.class) {
                        finalStatement.setLong(indexIn, (Long) actualValue);
                    } else {
                        plugin.console("{prefix}&cUnknown type: " + type);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                getPlugin().debug("Set value " + key + " with index " + ref.indexOut);
                ref.indexOut++;
            });

            statement.executeUpdate();
            plugin.debug("Created user " + user.getName() + " in database.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(statement);
        }
    }

    @Override
    public void saveUser(GamePlayer user) {
        PreparedStatement statement = null;

        HashMap<String, PlayerData.Data> schema = user.getSchema();

        try {
            StringBuilder columns = new StringBuilder();
            schema.forEach((key, value) -> columns.append(key).append(" = ?, "));
            columns.delete(columns.length() - 2, columns.length());
            String sql = String.format("UPDATE %s SET %s WHERE uuid = ?;", TABLE_DATA, columns);
            getPlugin().debug(sql);
            statement = connection.prepareStatement(sql);
            var ref = new Object() {
                int indexOut = 1;
            };

            PreparedStatement finalStatement = statement;
            schema.forEach((key, value) -> {
                int indexIn = ref.indexOut;
                try {
                    var type = value.getType();
                    var actualValue = value.getActualValue();
                    if (type == String.class) {
                        finalStatement.setString(indexIn, (String) actualValue);
                    } else if (type == Integer.class) {
                        finalStatement.setInt(indexIn, (Integer) actualValue);
                    } else if (type == Boolean.class) {
                        finalStatement.setBoolean(indexIn, (Boolean) actualValue);
                    } else if (type == Double.class) {
                        finalStatement.setDouble(indexIn, (Double) actualValue);
                    } else if (type == Long.class) {
                        finalStatement.setLong(indexIn, (Long) actualValue);
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
            statement.setString(ref.indexOut, user.getUuid().toString());
            statement.executeUpdate();
            plugin.debug("Saved user " + user.getName() + " to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(statement);
        }
    }
}
