package Data;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonMapper {
    static private JacksonMapper jacksonMapper = null;

    private JacksonMapper(){ }

    public static JacksonMapper getInstance(){
        if(jacksonMapper == null){
            jacksonMapper = new JacksonMapper();
        }
        return jacksonMapper;
    }

    public synchronized ObjectMapper getNetworkMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.EVERYTHING);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }
}
