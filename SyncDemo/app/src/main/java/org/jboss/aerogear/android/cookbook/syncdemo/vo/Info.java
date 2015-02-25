package org.jboss.aerogear.android.cookbook.syncdemo.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Info {
    
    private String name;
    private String profession;
    private List<String> hobbies;
    
    public Info(final String name, final String profession, final String... hobbies) {
        this.name = name;
        this.profession = profession;
        this.hobbies = new ArrayList<String>(Arrays.asList(hobbies));
    }

    public String getName() {
        return name;
    }

    public String getProfession() {
        return profession;
    }

    public List<String> getHobbies() {
        return hobbies;
    }
}
