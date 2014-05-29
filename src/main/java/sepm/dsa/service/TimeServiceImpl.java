package sepm.dsa.service;

import org.springframework.stereotype.Service;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.model.DSADate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.FileHandler;

public class TimeServiceImpl implements TimeService {
    private DSADate date;
    private Properties properties;

    public TimeServiceImpl() {
        try {
            properties = new Properties();
            Path path = Paths.get("properties");
            if(!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.exists(path);
            InputStream is = Files.newInputStream(path);
            properties.load(is);
            properties.get("time");
            is.close();
        } catch (IOException e) {
            throw new DSARuntimeException("Probleme beim Laden der Properties Datei! \n" + e.getMessage());
        }
    }

    @Override
    public DSADate getCurrentDate() {
        return date;
    }

    @Override
    public void setCurrentDate(DSADate dsaDate) {
        try {
            properties.put("time", dsaDate.getTimestamp()+"");
            OutputStream os = Files.newOutputStream(Paths.get("properties"));
            properties.store(os, "");
            os.close();
            date = dsaDate;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void forwardTime(int days) {
        // todo
    }
}
