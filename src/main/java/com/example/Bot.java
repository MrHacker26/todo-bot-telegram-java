package com.example;

import java.util.Timer;
import java.util.TimerTask;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
    Timer timer = new Timer();
    private String todo;
    private int ti;

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String in = update.getMessage().getText();
            if (in.contains("/todo")) {
                todo = in.replaceAll("/todo", "");
                if (todo.length() > 2) {
                    sendMsg(chatId, "✅ Todo has been created successfully!");
                } else {
                    sendMsg(chatId, "❌ Type /todo [yourtodo] to create one.");
                }
            } else if (in.contains("/show")) {
                if (todo == null || todo.length() < 1) {
                    sendMsg(chatId, "❌ No TODO to Show.\nType /todo [yourtodo] to create one.");
                } else {
                    sendMsg(chatId, "Your TODO list:\n➡️" + todo);

                }
            } else if (in.contains("/start")) {
                sendMsg(chatId, "WELCOME 👋😎 " + update.getMessage().getFrom().getFirstName() + ","
                        + "\nUse the following commands to use this bot😁:\n1. /todo (your todo name) 🙂\n2. /show to show added todo list 🤩\n3. /alert to get todo alerts 😌\n4. /cancel to cancel the alerts 😶\n5. /delete to delete your todo 😛");
            }

            else if (in.contains("/alert")) {
                ti = Integer.parseInt(in.replaceAll("/alert", "").trim());
                if (todo == null) {
                    sendMsg(chatId, "❌ No TODO detected.");
                } else if (ti < 2) {
                    sendMsg(chatId, "❌ Add seconds.");
                } else if (todo != null && ti > 2) {
                    sendMsg(chatId, "You will get alerts in ⏲️ " + ti + " seconds.");
                    alert(chatId);
                } else {
                    sendMsg(chatId, "❌ Error");
                }
            } else if (in.contains("/delete")) {
                if (todo == null) {
                    sendMsg(chatId, "❌ No TODO to delete.");
                } else {
                    sendMsg(chatId, "✅ Todo deleted successfully!");
                    todo = null;
                }
            } else if (in.contains("/cancel")) {
                timer.cancel();
                sendMsg(chatId, "Timer Stopped successfully");
                timer = new Timer();
            }

            else {
                sendMsg(chatId,
                        "❌ Error.\nUse the following commands to use this bot😁:\n1. /todo (your todo name) 🙂\n2. /show to show added todo list 🤩\n3. /alert to get todo alerts 😌\n4. /cancel to cancel the alerts 😶\n5. /delete to delete your todo 😛");
            }

        }
    }

    public synchronized void alert(String chatId) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendMsg(chatId, "💥 TODO ALERT!\n" + "✔" + todo);
            }
        }, 0, 1000 * ti);
    }

    @Override
    public String getBotUsername() {
        return "BOT_NAME";
    }

    @Override
    public String getBotToken() {
        return "BOT_API";
    }

    public synchronized void sendMsg(String chatId, String s) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();

        }
    }
}
