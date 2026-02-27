# Secure Desktop Password Manager

A fully offline credential vault with strong encryption, built with Java and JavaFX.

## Usage Guide

### Getting Started
1. **Create a Vault**: When you first launch the app, enter a strong master password and click "Create New Vault". This password is the key to all your data; if lost, the data cannot be recovered.
2. **The Vault File**: By default, your encrypted data is stored in `~/.lockfile.vault`. You can move this file or back it up as needed.

### Managing Credentials
- **Folders**: Use "New Folder" to organize your credentials (e.g., Work, Personal, Finance).
- **Entries**: Select a folder and click "New Entry" to add a credential. Fill in the title, username, and password.
- **Search**: Use the search bar at the top to quickly find credentials across the selected collection.
- **Copying**: Click the "Copy" buttons in the details pane. The data will be wiped from your clipboard automatically after a short period (10-60 seconds) for security.

### Two-Factor Authentication (TOTP)
If a service offers 2FA via an app (like Google Authenticator), you can save the "Secret Key" (Base32 string) in the "TOTP Secret" field of an entry. Lockfile will then generate the 6-digit codes for you.

## Features
- Fully offline functionality.
- AES-GCM encryption for entries.
- Master-password derivation using PBKDF2 via Java Cryptography Extension (JCE).
- Hierarchical folders.
- Search entries by title, username, or URL.
- Clipboard integration with auto-clear.
- Import/export of encrypted JSON.
- Built-in TOTP generator (GUI-based).
- Password history tracking and revert.
- Modern high-contrast dark theme.
- Cross-platform support (Linux, macOS, Windows).

## Tech Stack
- Java 25+ (OpenJDK)
- JavaFX 21
- Gradle
- Jackson (for JSON serialization)

## Build and Run
```bash
gradle build
gradle run
```