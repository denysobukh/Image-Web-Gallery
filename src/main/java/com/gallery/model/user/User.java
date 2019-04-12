package com.gallery.model.user;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String username;
    private Type type;
    private String password;
    @OneToOne(cascade = CascadeType.ALL)
    private UserPreferences preferences;
    protected User() {
    }

    private User(String name, String username, Type type, String password, UserPreferences preferences) {
        this.name = name;
        this.username = username;
        this.type = type;
        this.password = password;
        this.preferences = preferences;
    }

    public UserPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public enum Type {
        ADMIN, USER, GUEST;
    }

    public static class UserBuilder {
        private String name;
        private String username;
        private Type type;
        private String password;
        private UserPreferences userPreferences = new UserPreferences();

        public UserBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder setUsername(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder setType(Type type) {
            this.type = type;
            return this;
        }

        public UserBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder setUserPreferences(UserPreferences userPreferences) {
            this.userPreferences = userPreferences;
            return this;
        }

        public User build() {
            return new User(this.name, this.username, this.type, this.password, this.userPreferences);
        }
    }

    @Override
    public String toString() {
        return String.format("User[%d; %s; %s; %s]", id, name, type, preferences);
    }
}
