package com.beshton.payroll;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

@Service
public class AudioServiceImpl implements AudioService {

    private static final String BUCKET_NAME = "texttoaudiogroup3";
//    private static final String AUDIO_FILE_NAME = "output1.mp3";
    private static final Logger logger = LoggerFactory.getLogger(AudioServiceImpl.class);

    @Override
    public void generateAndUploadAudio(String text, String AUDIO_FILE_NAME) {
        try {
            logger.info("Starting text-to-speech synthesis for text: {}", text);
            ByteString audioContents = synthesizeText(text, AUDIO_FILE_NAME);
            saveAudioToFile(audioContents, AUDIO_FILE_NAME);
            uploadToS3(AUDIO_FILE_NAME);
            logger.info("Audio generated and uploaded successfully.");
        } catch (Exception e) {
            logger.error("Error generating and uploading audio: ", e);
        }
    }

    /**
     * Synthesizes text using the Google Text-to-Speech API.
     *
     * @param text the raw text to be synthesized. (e.g., "Hello there!")
     * @throws Exception on TextToSpeechClient Errors.
     */
    public static ByteString synthesizeText(String text, String AUDIO_FILE_NAME) throws Exception {
        // Instantiates a client
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // Set the text input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            // Build the voice request
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("en-US") // languageCode = "en_us"
                    .setSsmlGender(SsmlVoiceGender.FEMALE) // ssmlVoiceGender = SsmlVoiceGender.FEMALE
                    .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3) // MP3 audio.
                    .build();

            // Perform the text-to-speech request
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            // Write the response to the output file.
            try (OutputStream out = new FileOutputStream(AUDIO_FILE_NAME)) {
                out.write(audioContents.toByteArray());
                logger.info("Audio content written to file \"{}\"", AUDIO_FILE_NAME);
                return audioContents;
            }
        }
    }

    public void saveAudioToFile(ByteString audioContents, String AUDIO_FILE_NAME) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(AUDIO_FILE_NAME)) {
            outputStream.write(audioContents.toByteArray());
            logger.info("Audio file saved successfully.");
        }
    }

    public void uploadToS3(String AUDIO_FILE_NAME) {
        try {
            // Specify the region your bucket is in
            Region region = Region.US_EAST_1; // Change this to your bucket's region

            S3Client s3 = S3Client.builder()
                    .region(region)
                    .credentialsProvider(ProfileCredentialsProvider.create())
                    .build();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(AUDIO_FILE_NAME)
                    .build();

            s3.putObject(putObjectRequest, RequestBody.fromFile(Paths.get(AUDIO_FILE_NAME)));
            logger.info("Path name \"{}\" successfully.", Paths.get(AUDIO_FILE_NAME));
            logger.info("Audio file uploaded to S3 bucket \"{}\" successfully.", BUCKET_NAME);
        } catch (Exception e) {
            logger.error("Error uploading file to S3: ", e);
        }
    }
}
