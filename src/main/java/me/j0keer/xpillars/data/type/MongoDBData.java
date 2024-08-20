package me.j0keer.xpillars.data.type;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.data.Database;
import me.j0keer.xpillars.player.GamePlayer;
import me.j0keer.xpillars.player.PlayerData;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class MongoDBData extends Database {
    private MongoCollection<Document> collection;
    private MongoDatabase db;
    private MongoClient client;

    public MongoDBData(XPillars plugin) {
        super(plugin);
    }

    @Override
    protected String getType() {
        return "MongoDB";
    }

    @Override
    public void connect() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("database.mongodb");
        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(section.getString("uri"))).build();

        client = MongoClients.create(settings);
        db = client.getDatabase(section.getString("database"));
        collection = db.getCollection("users");

        plugin.console("{prefix}&7Connected to MongoDB.");
    }

    @Override
    public void disconnect() {
        client.close();
        plugin.console("{prefix}&7Disconnected from MongoDB.");
    }

    @Override
    public void loadUser(GamePlayer user) {
        Document query = new Document("uuid", user.getUuid().toString());
        Document result = collection.find(query).first();

        if (result != null) {
            HashMap<String, PlayerData.Data> schema = user.getSchema();

            schema.forEach((key, value) -> {
                Object data = result.get(key);
                if (data != null) {
                    var type = value.getType();
                    if (type == String.class) {
                        value.set((String) data);
                    } else if (type == Integer.class) {
                        value.set((Integer) data);
                    } else if (type == Boolean.class) {
                        value.set((Boolean) data);
                    } else if (type == Double.class) {
                        value.set((Double) data);
                    } else if (type == Long.class) {
                        value.set((Long) data);
                    } else {
                        plugin.console("{prefix}&cUnknown type: " + type);
                    }
                }
            });

            plugin.debug("Loaded user " + user.getName() + " from MongoDB.");
        } else {
            user.setSaved(false);
            plugin.debug("User " + user.getUuid() + " not found in MongoDB. A new entry will be created upon save.");
            createUser(user);
        }
    }

    public void createUser(GamePlayer user) {
        HashMap<String, PlayerData.Data> schema = user.getSchema();
        Document document = new Document("uuid", user.getUuid().toString());

        schema.forEach((key, value) -> document.append(key, value.getActualValue()));

        collection.insertOne(document);
        plugin.debug("Created user " + user.getName() + " in MongoDB.");
    }

    @Override
    public void saveUser(GamePlayer user) {
        Document query = new Document("uuid", user.getUuid().toString());
        Document update = new Document();
        HashMap<String, PlayerData.Data> schema = user.getSchema();

        schema.forEach((key, value) -> update.append(key, value.getActualValue()));

        Document set = new Document("$set", update);
        collection.updateOne(query, set);

        plugin.debug("Saved user " + user.getName() + " to MongoDB.");
    }
}