CREATE TABLE User (
    userId TEXT PRIMARY KEY NOT NULL UNIQUE,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    phoneNo TEXT NOT NULL,
    email TEXT NOT NULL,
    createdAt TEXT DEFAULT (datetime('now')) NOT NULL,
    updatedAt TEXT
);

CREATE TABLE Todo (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    userId TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    status TEXT NOT NULL,
    createdAt TEXT DEFAULT (datetime('now')) NOT NULL,
    updatedAt TEXT,
    FOREIGN KEY (userId) REFERENCES User(userId)
);
