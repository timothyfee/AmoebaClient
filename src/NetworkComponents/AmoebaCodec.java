package NetworkComponents;

import Connection.Connection;
import Mercury.Codec;
import DataTransferUnits.KeyValuePair;
import java.util.ArrayList;

public class AmoebaCodec implements Codec{

    private final String beginStreamTag = "begin:stream;";
    private final String endStreamTag = "end:stream;";
    
    @Override
    public boolean checkHeader(String data, Connection connection) {
        if (!data.contains(beginStreamTag) && !data.equals("")){
            System.out.println("Incorrect format: connection " + connection.connectionId);
            return false;
        }
        return true;
    }

    @Override
    public boolean checkFooter(String data) {
        return data.contains(endStreamTag);
    }

    @Override
    public ArrayList<KeyValuePair> decode(String dataStream) {
        dataStream = filterDataStream(dataStream);
        ArrayList<KeyValuePair> pairs = new ArrayList<>();
        String buffer = "";
        String field = "";
        String value;
        for (int i = 0; i < dataStream.length(); i++)
        {
            char c = dataStream.charAt(i);
            switch(c)
            {
            case '#':
                i++;
                buffer += dataStream.charAt(i);
                continue;
            case ':':
                field = buffer;
                buffer = "";
                break;
            case ';':
                value = buffer;
                buffer = "";
                pairs.add(new KeyValuePair(field, value));
                break;
            default:
                buffer += c;
            }
        }
        return pairs;
    }

    private String filterDataStream(String dataStream){
        dataStream = dataStream.replace(beginStreamTag, "");
        dataStream = dataStream.replace(endStreamTag, "");
        return dataStream;
    }
    
    @Override
    public String encode(ArrayList<KeyValuePair> pairs) {
        String stream = "";
        String field;
        String value;
        for (KeyValuePair pair : pairs){
            field = escapeCharacters(pair.field);
            value = escapeCharacters(pair.value);
            stream += field + ':' + value + ';';
        }        
        return prepareDataStream(stream);
    }
    
    private String prepareDataStream(String dataStream){
        return beginStreamTag + dataStream + endStreamTag;
    }
    
    private String escapeCharacters(String key){
        String result = key.replace("#", "##");
        result = result.replace(":", "#:");
        result = result.replace(";", "#;");
        return result;
    }
}
