# Secure Desktop Password Manager

A fully offline credential vault with strong encryption, built with Java and JavaFX.

## Features
- Fully offline functionality.
- AES-GCM encryption for entries.
- Master-password derivation using PBKDF2 via Java Cryptography Extension (JCE).
- Hierarchical folders.
- Search entries by title, username, or URL.
- Clipboard integration with auto-clear.
- Import/export of encrypted JSON.
- Built-in TOTP generator (GUI-based).
- Cross-platform support (Linux, macOS, Windows).

## Tech Stack
- Java 21+
- JavaFX 21
- Maven
- Jackson (for JSON serialization)

## Build and Run
```bash
gradle build
gradle run
```