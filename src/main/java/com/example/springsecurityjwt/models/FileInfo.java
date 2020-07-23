package com.example.springsecurityjwt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class FileInfo {
    @Id
    @Length(max = 100)
    private String name;
    private String url;

    public FileInfo(String name){
        this.name = name;
    }

}
