package com.scolastico.discord_exe.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.scolastico.discord_exe.etc.ErrorHandler;

import java.io.*;

public class ConfigHandler {

    private Object _configObject;
    private String _filename;
    private File _file;
    private Gson _gson;

    public ConfigHandler(Object configObject, String filename, Boolean throwErrorOnCreation) throws IOException {

        _filename = filename;
        _file = new File(filename);
        _gson = new GsonBuilder().setPrettyPrinting().create();

        if (_file.exists()) {
            _configObject = _gson.fromJson(new FileReader(_file), configObject.getClass());
        } else {
            writeStringToFile(_gson.toJson(configObject));
            ErrorHandler.getInstance().handleFatal(new Exception("Config created! Please edit it and then restart!"));
        }

    }

    public ConfigHandler(Object configObject, String filename) throws Exception {

        _filename = filename;
        _file = new File(filename);
        _gson = new GsonBuilder().setPrettyPrinting().create();

        if (_file.exists()) {
            _configObject = _gson.fromJson(new FileReader(_file), configObject.getClass());
        } else {
            writeStringToFile(_gson.toJson(configObject));
            _configObject = configObject;
        }

    }

    private void writeStringToFile(String string) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(_file));
        writer.write(string);
        writer.close();
    }

    public Object getConfigObject() {
        return _configObject;
    }

    public void setConfigObject(Object configObject) {
        this._configObject = configObject;
    }

    public void saveConfigObject() throws IOException {
        _gson.toJson(_configObject, new FileWriter(_file));
    }

    public String getFilename() {
        return _filename;
    }
}
