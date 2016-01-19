package NetworkComponents;

import Mercury.Interpreter;
import DataTransferUnits.*;
import Messages.*;
import NetworkMessages.*;
import java.util.ArrayList;

public class AmoebaInterpreter implements Interpreter {

    @Override
    public ArrayList<NetworkMessage> parse(ArrayList<KeyValuePair> pairs) {
        int beginSection = 0;
        int endSection;
        ArrayList<NetworkMessage> messages = new ArrayList<>();
        for (int i = 0; i < pairs.size(); i++) {
            KeyValuePair pair = pairs.get(i);
            switch (pair.field) {
                case "begin":
                    beginSection = i;
                    break;
                case "end":
                    endSection = i;
                    parseSection(beginSection, endSection, pairs, messages);
                    break;
            }
        }
        return messages;
    }

    private void parseSection(int begin, int end, final ArrayList<KeyValuePair> pairs, ArrayList<NetworkMessage> messages) {
        String messageType = pairs.get(begin).value;
        switch (messageType) {
            case "PelletPositionMessage": {
                double x = new Double(getNumber("x", begin, end, pairs));
                double y = new Double(getNumber("y", begin, end, pairs));
                long id = new Long(getNumber("id", begin, end, pairs));
                messages.add(new PelletPositionMessage(x, y, id));
            }
            break;
            case "BlobStateMessage": {
                double x = new Double(getNumber("x", begin, end, pairs));
                double y = new Double(getNumber("y", begin, end, pairs));
                double size = new Double(getNumber("size", begin, end, pairs));
                String color = getValue("color", begin, end, pairs);
                String username = getValue("username", begin, end, pairs);
                long id = new Long(getNumber("id", begin, end, pairs));
                messages.add(new BlobStateMessage(x, y, size, color, username, id));
            }
            break;
            case "PingMessage": {
                messages.add(new PingMessage());
            }
            break;
            case "HighScoreMessage": {
                String text = getValue("text", begin, end, pairs);
                messages.add(new HighScoreMessage(text));
            }
            break;
            case "CurrentScoreMessage": {
                String text = getValue("text", begin, end, pairs);
                messages.add(new CurrentScoreMessage(text));
            }
            break;
            case "ChatMessage": {
                String username = getValue("username", begin, end, pairs);
                String text = getValue("text", begin, end, pairs);
                messages.add(new ChatMessage(username, text));
            }
            break;
            case "PlainTextMessage": {
                String text = getValue("text", begin, end, pairs);
                messages.add(new PlainTextMessage(text));
            }
            break;
        }
    }

    private String getValue(String field, int begin, int end, final ArrayList<KeyValuePair> pairs) {
        for (int i = begin; i < end; i++) {
            if (pairs.get(i).field.equals(field)) {
                return pairs.get(i).value;
            }
        }
        return "";
    }

    private String getNumber(String field, int begin, int end, final ArrayList<KeyValuePair> pairs){
        String value = getValue(field, begin, end, pairs);
        return !value.isEmpty() ? value : "0";
    }
    
    @Override
    public ArrayList<KeyValuePair> compile(ArrayList<NetworkMessage> messages) {
        ArrayList<KeyValuePair> pairs = new ArrayList<>();
        for (NetworkMessage message : messages) {
            compileMessage(message, pairs);
        }
        return pairs;
    }

    private void compileMessage(NetworkMessage message, ArrayList<KeyValuePair> pairs) {
        String messageType = message.getClass().getSimpleName();
        switch (messageType) {
            case "MoveTowardCoordinatesMessage": {
                MoveTowardCoordinatesMessage m = (MoveTowardCoordinatesMessage) message;
                pairs.add(new KeyValuePair("begin", messageType));
                pairs.add(new KeyValuePair("x", Double.toString(m.x)));
                pairs.add(new KeyValuePair("y", Double.toString(m.y)));
                pairs.add(new KeyValuePair("end", messageType));
            }
            break;
            case "BoostMessage":
            case "SplitMessage":
            case "PingMessage":
            case "LogoutMessage": {
                pairs.add(new KeyValuePair("begin", messageType));
                pairs.add(new KeyValuePair("end", messageType));
            }
            break;
            case "ChatMessage": {
                ChatMessage m = (ChatMessage) message;
                pairs.add(new KeyValuePair("begin", messageType));
                pairs.add(new KeyValuePair("username", m.username));
                pairs.add(new KeyValuePair("text", m.text));
                pairs.add(new KeyValuePair("end", messageType));
            }
            break;
            case "LoginMessage": {
                LoginMessage m = (LoginMessage) message;
                pairs.add(new KeyValuePair("begin", messageType));
                pairs.add(new KeyValuePair("username", m.username));
                pairs.add(new KeyValuePair("password", m.password));
                pairs.add(new KeyValuePair("end", messageType));
            }
            break;
            case "SetBlobPropertiesMessage":{
                SetBlobPropertiesMessage m = (SetBlobPropertiesMessage) message;
                pairs.add(new KeyValuePair("begin", messageType));
                pairs.addAll(m.properties);
                pairs.add(new KeyValuePair("end", messageType));
            }
            break;
            case "MoveInDirectionMessage": {
                MoveInDirectionMessage m = (MoveInDirectionMessage) message;
                pairs.add(new KeyValuePair("begin", messageType));
                pairs.add(new KeyValuePair("direction", Double.toString(m.direction)));
                pairs.add(new KeyValuePair("end", messageType));
            }
            break;
            case "PlainTextMessage": {
                PlainTextMessage m = (PlainTextMessage) message;
                pairs.add(new KeyValuePair("begin", messageType));
                pairs.add(new KeyValuePair("text", m.text));
                pairs.add(new KeyValuePair("end", messageType));
            }
            break;
        }
    }
}
