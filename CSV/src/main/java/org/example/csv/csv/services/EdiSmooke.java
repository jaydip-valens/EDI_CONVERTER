//package org.example.csv.csv.services;
//
//import org.dhatim.Smooks;
//import org.dhatim.SmooksException;
//import org.dhatim.container.ExecutionContext;
//import org.dhatim.io.StreamUtils;
//import org.springframework.stereotype.Service;
//import org.xml.sax.SAXException;
//
//import javax.xml.transform.stream.StreamResult;
//import javax.xml.transform.stream.StreamSource;
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//
//@Service
//public class EdiSmooke {
//
//    public String convertEdiToCsv(InputStream ediStream) throws IOException, SmooksException, SAXException {
//        Smooks smooks = new Smooks("smooks-config.xml"); // Smooks configuration file
//        ExecutionContext executionContext = smooks.createExecutionContext();
//
//        try {
//            StringWriter writer = new StringWriter();
//            InputStreamReader reader = new InputStreamReader(ediStream, StandardCharsets.UTF_8);
//            smooks.filterSource(executionContext, new StreamSource(new StringReader(StreamUtils.readStreamAsString(ediStream))), new StreamResult(writer));
//            return writer.toString();
//        } finally {
//            smooks.close();
//        }
//    }
//}
