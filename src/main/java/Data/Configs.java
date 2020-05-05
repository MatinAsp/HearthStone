package Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

class Configs extends Properties {
    int readInteger(String name){
        return Integer.parseInt(this.getProperty(name));
    }

    boolean readBoolean (String name){
        return Boolean.parseBoolean(this.getProperty(name));
    }

    public double readDouble(String name) {
        return Double.parseDouble(this.getProperty(name));
    }


    public List<Integer> readIntegerList(String name){
        List<Integer> list = new ArrayList<>();
        for(String string: readStringList(name)){
            list.add(Integer.parseInt(string));
        }
        return list;
    }

    public List<String> readStringList(String name){
        String listString = this.getProperty(name);
        String[] str = listString.split(",");
        return Arrays.asList(str);
    }
}
