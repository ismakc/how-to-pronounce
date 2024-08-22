# How to Pronounce

## Description

The `How to Pronounce` application is a command-line tool that plays the pronunciation of English words using MP3 audio files. The application supports two different accents (American and British) and two playback speeds. It uses Google's pronunciation service to fetch the audio files.

## Requirements

- Java 17

## Usage

To run the application, use the following command:

```sh
java -jar say.jar [options] <word1> <word2> ... <wordN>
```

### Options

- `-gb`: Uses the British accent (default is American accent).
- `-s`: Plays the audio at a slower speed (default is normal speed).

### Examples

1. Play the pronunciation of the words "hello" and "world" with an American accent and normal speed:

    ```sh
    java -jar say.jar hello world
    ```

2. Play the pronunciation of the word "color" with a British accent and normal speed:

    ```sh
    java -jar say.jar -gb color
    ```

3. Play the pronunciation of the word "slow" with an American accent and fast speed:

    ```sh
    java -jar say.jar -s fast
    ```

4. Play the pronunciation of the words "quick" and "brown" with a British accent and slow speed:

    ```sh
    java -jar say.jar -gb -s quick brown
    ```

## Notes

- Each word must have at least 2 characters.
- The application will attempt to download and play the corresponding audio file for each word from a remote server using Google's pronunciation service. If the audio file is not found, an error message will be displayed.

## Creating a Shortcut for Windows

You can create a `say.bat` file in the `C:\Windows\System32` directory with the following content to invoke the application concisely:

```bat
@echo off
set args=%*
java -jar %userprofile%\apps\say.jar %args%
```

## Creating a Shortcut for Linux

You can create a `say` script in a directory included in your `PATH` (e.g., `/usr/local/bin`) with the following content to invoke the application concisely:

```sh
#!/bin/bash
java -jar ~/apps/say.jar "$@"
```

Make sure to give the script execute permissions:

```sh
chmod +x /usr/local/bin/say
```

## Example of Invocation Using Shortcuts

### Windows

To play the pronunciation of the word "example" with a British accent and fast speed using the `say.bat` shortcut:

```sh
say -gb -s example
```

### Linux

To play the pronunciation of the word "example" with a British accent and fast speed using the `say` script:

```sh
say -gb -s example
```
```