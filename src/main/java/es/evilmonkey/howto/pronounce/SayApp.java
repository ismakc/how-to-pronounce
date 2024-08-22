package es.evilmonkey.howto.pronounce;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

public class SayApp {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar TextToSpeech.jar [-gb] [-s] <word1> <word2> ... <wordN>");
            return;
        }

        String accent = "us";
        String speed = "1";
        int startIndex = 0;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-gb")) {
                accent = "gb";
                startIndex++;
            } else if (arg.equalsIgnoreCase("-s")) {
                speed = "2";
                startIndex++;
            }
        }

        for (int i = startIndex; i < args.length; i++) {
            String word = args[i];
            if (word.length() < 2) {
                System.out.println("Word must have at least 2 characters: " + word);
                continue;
            }
            String urlStr = String.format("https://ssl.gstatic.com/dictionary/static/pronunciation/2024-04-19/audio/%s/%s_en_%s_%s.mp3", word.substring(0, 2), word.toLowerCase(), accent, speed);
            try {
                playAudioFromURL(urlStr);
            } catch (Exception ex) {
                System.out.println("Failed to play audio for word: " + word + ". Reason: " + ex.getMessage());
            }
        }
    }

    private static void playAudioFromURL(String urlStr) throws IOException, UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).proxy(getProxyFromEnvironment().map(p -> ProxySelector.of((InetSocketAddress) p.address())).orElse(ProxySelector.getDefault())).build();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlStr)).timeout(Duration.ofSeconds(5)).build();

        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() == 200) {
            try (InputStream inputStream = new BufferedInputStream(response.body()); AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputStream)) {

                AudioFormat baseFormat = audioStream.getFormat();
                AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);

                try (AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, audioStream)) {
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
                    try (SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
                        line.open(decodedFormat);
                        line.start();

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = decodedStream.read(buffer)) != -1) {
                            line.write(buffer, 0, bytesRead);
                        }

                        line.drain();
                    }
                }
            }
        } else {
            System.out.println("Audio file not found: " + urlStr);
        }
    }

    private static Optional<Proxy> getProxyFromEnvironment() {
        String proxyUrl = Optional.ofNullable(System.getenv("http_proxy")).orElse(System.getenv("https_proxy"));
        return proxyUrl != null && !proxyUrl.isEmpty() ? Optional.of(parseProxy(proxyUrl)) : Optional.empty();
    }

    private static Proxy parseProxy(String proxyUrl) {
        URI uri = URI.create(proxyUrl);
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(uri.getHost(), uri.getPort()));
    }
}