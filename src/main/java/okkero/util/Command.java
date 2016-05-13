package okkero.util;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class Command
{

    @Getter
    @SerializedName("commandname")
    private String commandName;

}
