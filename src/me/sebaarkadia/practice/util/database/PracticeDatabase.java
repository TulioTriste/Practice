package me.sebaarkadia.practice.util.database;

import java.util.List;
import java.util.Arrays;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import me.sebaarkadia.practice.Practice;

import com.mongodb.MongoClient;

public class PracticeDatabase
{
    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection profiles;
    private MongoCollection kits;
    
    public PracticeDatabase(final Practice main) {
        this.client = (main.getConfig().getBoolean("DATABASE.MONGO.AUTHENTICATION.ENABLED") ? new MongoClient(new ServerAddress(main.getConfig().getString("DATABASE.MONGO.HOST"), main.getConfig().getInt("DATABASE.MONGO.PORT")), (List)Arrays.asList(MongoCredential.createCredential(main.getConfig().getString("DATABASE.MONGO.AUTHENTICATION.USER"), main.getConfig().getString("DATABASE.MONGO.AUTHENTICATION.DATABASE"), main.getConfig().getString("DATABASE.MONGO.AUTHENTICATION.PASSWORD").toCharArray()))) : new MongoClient(new ServerAddress(main.getConfig().getString("DATABASE.MONGO.HOST"), main.getConfig().getInt("DATABASE.MONGO.PORT"))));
        this.database = this.client.getDatabase("practice");
        this.profiles = this.database.getCollection("profiles");
        this.kits = this.database.getCollection("kits");
    }
    
    public MongoClient getClient() {
        return this.client;
    }
    
    public MongoDatabase getDatabase() {
        return this.database;
    }
    
    public MongoCollection getProfiles() {
        return this.profiles;
    }
    
    public MongoCollection getKits() {
        return this.kits;
    }
}
