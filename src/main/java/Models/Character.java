package Models;

import Data.Converter;
import Interfaces.Cloneable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.security.SecureRandom;

@Entity
public class Character implements Cloneable {
    @Id
    @Column
    private String name;
    @Convert(converter = Converter.class)
    private int id = new SecureRandom().nextInt();

    public Character(){ }

    public Character (String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId(){
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Character newOne() {
        return new Character(this.name);
    }

    public void setId(int id) {
        this.id = id;
    }
}
