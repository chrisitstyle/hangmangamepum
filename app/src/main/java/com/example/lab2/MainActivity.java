package com.example.lab2;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String[] WORDS = {
            "komputer", "procesor","java","zasilacz"
    };
    private static final int MAX_ATTEMPTS = 10;
    private String secretWord;
    private char[] guessedLetters;
    private int attempts;
    private Set<Character> uniqueCharacters;
    private TextView wordTextView;
    private TextView infoTextView;
    private EditText letterEditText;
    private Button checkButton;
    private TextView usedLetters;

    private ImageView wisielecImage;

    private TextView attemptsCounterTextView;

    private String buttonText;

    MediaPlayer mediaPlayer = new MediaPlayer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wordTextView = findViewById(R.id.wordTextView);
        infoTextView = findViewById(R.id.infoTextView);
        letterEditText = findViewById(R.id.letterEditText);
        checkButton = findViewById(R.id.checkButton);
        usedLetters = findViewById(R.id.usedLettersText);
        attemptsCounterTextView = findViewById(R.id.attemptsCounterTextView);
        wisielecImage = findViewById(R.id.wisielecImageView);
     
        setGameSettings();
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonText = (String) checkButton.getText();
                if (buttonText.equals("Restart gry")) {
                        setGameSettings();
                } else {
                    String input = letterEditText.getText().toString().trim();

                    if (input.length() != 1) {
                        letterEditText.setText("");
                        infoTextView.setText("Podaj jedną literę.");
                    } else {
                        char guess = input.charAt(0);
                        letterEditText.setText("");

                        if (uniqueCharacters.contains(guess)) {
                            infoTextView.setText("Już zgadłeś tę literę.");
                        } else {
                            uniqueCharacters.add(guess);
                            if (checkGuess(guess)) {
                                infoTextView.setText("Dobrze! Litera jest w słowie.");
                                playSound(R.raw.poprawna);
                            } else {
                                infoTextView.setText("Nieprawidłowa litera.");
                                playSound(R.raw.niepoprawna);

                                attempts++;
                                attemptsCounterTextView.setText("Pozostało prób: " + String.valueOf(MAX_ATTEMPTS - attempts));
                                updateHangmanImage(); // update image after wrong attempt
                            }

                            if (attempts >= MAX_ATTEMPTS) {
                                infoTextView.setText("Przegrałeś. Słowo to: " + secretWord);
                                playSound(R.raw.przegrana);
                                attemptsCounterTextView.setText("Pozostało prób: " + String.valueOf(0));
                                checkButton.setText("Restart gry");
                                letterEditText.setEnabled(false);
                            }

                            if (isWordGuessed()) {
                                playSound(R.raw.wygrana);
                                infoTextView.setText("Gratulacje! Odgadłeś słowo.");
                                checkButton.setText("Restart gry");
                                letterEditText.setEnabled(false);

                            }
                        }
                        displayWord();
                        updateUsedLetters();
                    }
                }
            }
        });


    }

    private String chooseRandomWord() {
        Random random = new Random();
        int index = random.nextInt(WORDS.length);
        return WORDS[index];
    }

    private void displayWord() {
        StringBuilder display = new StringBuilder();
        for (char letter : secretWord.toCharArray()) {
            if (containsLetter(letter)) {
                display.append(letter);
            } else {
                display.append("_");
            }
            display.append(" ");
        }
        wordTextView.setText("Słowo: " + display.toString());
    }

    private boolean containsLetter(char letter) {
        for (char guessedLetter : guessedLetters) {
            if (guessedLetter == letter) {
                return true;
            }
        }
        return false;
    }

    private boolean checkGuess(char guess) {
        boolean correctGuess = false;
        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) == guess) {
                guessedLetters[i] = guess;
                correctGuess = true;
            }
        }
        return correctGuess;
    }

    private boolean isWordGuessed() {
        for (char letter : guessedLetters) {
            if (letter == '\0') {
                return false;
            }
        }
        return true;
    }

    private void updateUsedLetters() {
        StringBuilder usedLettersBuilder = new StringBuilder();
        for (char letter : uniqueCharacters) {
            usedLettersBuilder.append(letter).append(", ");
        }
        String usedLettersString = usedLettersBuilder.toString();
        if (usedLettersString.length() > 0) {
            usedLettersString = usedLettersString.substring(0, usedLettersString.length() - 2);
        }
        usedLetters.setText("Użyte litery: " + usedLettersString);
    }

    // method to updating image for hangman
    private void updateHangmanImage() {
        int imageId = getResources().getIdentifier("image" + attempts, "drawable", getPackageName());
        wisielecImage.setImageResource(imageId);
    }


    private void playSound(int soundResource) {
        try {
            mediaPlayer.reset();
            mediaPlayer = MediaPlayer.create(this, soundResource);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        if (mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;
        }
        super.onDestroy();
    }

    void setGameSettings(){
        playSound(R.raw.start);
        secretWord = chooseRandomWord();
        guessedLetters = new char[secretWord.length()];
        attempts = 0;
        uniqueCharacters = new HashSet<>();
        displayWord();
        attemptsCounterTextView.setText("Pozostało prób: " + String.valueOf(MAX_ATTEMPTS));
        wisielecImage.setImageResource(R.drawable.image0);
        usedLetters.setText(" ");
        letterEditText.setEnabled(true);
        infoTextView.setText(" ");
        checkButton.setText("Sprawdź");


    }
}
