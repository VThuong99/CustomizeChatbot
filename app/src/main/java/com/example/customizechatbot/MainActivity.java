package com.example.customizechatbot;

import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.content.Context; // Import Context
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvChatMessages;
    private EditText etMessageInput;
    private Button btnSendMessage;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messageList;
    private RequestQueue requestQueue;

    private static final String GEMINI_API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"; // Dùng gemini-pro cho chất lượng tốt hơn
    private static final String TOM_PERSONA_PROMPT_PREFIX = "Bạn là Tom, một người bạn thân thiện, tích cực, luôn lắng nghe và đưa ra lời khuyên chân thành, đồng cảm. Hãy duy trì vai trò này trong suốt cuộc trò chuyện.\n\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvChatMessages = findViewById(R.id.rvChatMessages);
        etMessageInput = findViewById(R.id.etMessageInput);
        btnSendMessage = findViewById(R.id.btnSendMessage);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChatMessages.setLayoutManager(layoutManager);
        rvChatMessages.setAdapter(messageAdapter);

        requestQueue = Volley.newRequestQueue(this);

        addBotMessage("Chào bạn!");

        btnSendMessage.setOnClickListener(v -> {
            String userMessageText = etMessageInput.getText().toString().trim();
            if (!userMessageText.isEmpty()) {
                addUserMessage(userMessageText);
                etMessageInput.setText("");
                hideKeyboard();
                sendMessageToGemini(userMessageText);
            }
        });
    }

    private void addUserMessage(String message) {
        messageList.add(new Message(message, true)); // true = isUser
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        rvChatMessages.scrollToPosition(messageList.size() - 1);
    }

    private void addBotMessage(String message) {
        messageList.add(new Message(message, false)); // false = isUser (là bot)
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        rvChatMessages.scrollToPosition(messageList.size() - 1);
    }

    private void sendMessageToGemini(String userMessage) {
        addBotMessage("Tom đang nghĩ...");

        String apiKey = BuildConfig.GEMINI_API_KEY;
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.equals("YOUR_API_KEY_HERE")) {
            handleApiError("Error API Key.");
            return;
        }
        String fullApiUrl = GEMINI_API_BASE_URL + "?key=" + apiKey;

        try {
            JSONObject requestBody = new JSONObject();
            JSONArray contents = new JSONArray();

            JSONObject personaPromptContent = new JSONObject();
            personaPromptContent.put("role", "user");
            JSONArray personaPromptParts = new JSONArray();
            JSONObject personaPromptPart = new JSONObject();
            personaPromptPart.put("text", TOM_PERSONA_PROMPT_PREFIX);
            personaPromptParts.put(personaPromptPart);
            personaPromptContent.put("parts", personaPromptParts);
            contents.put(personaPromptContent);

            // Add message history
            int maxHistory = 10;
            int messageListSizeBeforeThinking = messageList.size() - 1;
            int startIndex = Math.max(0, messageListSizeBeforeThinking - maxHistory);

            for (int i = startIndex; i < messageListSizeBeforeThinking; i++) {
                Message msg = messageList.get(i);
                JSONObject messageContent = new JSONObject();
                messageContent.put("role", msg.isUser() ? "user" : "model");
                JSONArray messageParts = new JSONArray();
                JSONObject messagePart = new JSONObject();
                messagePart.put("text", msg.getContent());
                messageParts.put(messagePart);
                messageContent.put("parts", messageParts);
                contents.put(messageContent);
            }

            requestBody.put("contents", contents);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, fullApiUrl, requestBody,
                    response -> {
                        removeLastBotMessage();
                        try {
                            String botResponse = response.getJSONArray("candidates")
                                    .getJSONObject(0).getJSONObject("content")
                                    .getJSONArray("parts").getJSONObject(0).getString("text");
                            addBotMessage(botResponse.trim());
                        } catch (Exception e) {
                            handleApiError("Error parsing response: " + e.getMessage());
                        }
                    },
                    error -> {
                        removeLastBotMessage();
                        handleApiError("API Error: " + getVolleyError(error));
                    }) {
                @Override
                public Map<String, String> getHeaders() { // Override getHeaders
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            requestQueue.add(request);

        } catch (Exception e) {
            removeLastBotMessage();
            handleApiError("Error: " + e.getMessage());
        }
    }

    private void handleApiError(String errorMessage) {
        Log.e("MainActivity", errorMessage);
        addBotMessage("Xin lỗi, mình đang gặp chút trục trặc. Bạn thử lại sau nhé! (" + errorMessage + ")");
    }

    private void removeLastBotMessage() {
        if (!messageList.isEmpty() && !messageList.get(messageList.size() - 1).isUser()) {
            int lastBotMessageIndex = messageList.size() - 1;
            messageList.remove(lastBotMessageIndex);
            messageAdapter.notifyItemRemoved(lastBotMessageIndex);
        }
    }


    private String getVolleyError(VolleyError error) {
        String message = error.getMessage();
        if (error.networkResponse != null && error.networkResponse.data != null) {
            try {
                String responseData = new String(error.networkResponse.data, "UTF-8");
                message += "\nChi tiết: " + responseData;
            } catch (Exception e) {
                Log.e("MainActivity", "Error reading error response data", e);
            }
        }
        return message != null ? message : "Unknown Volley Error";
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}