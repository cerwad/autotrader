package fr.ced.autotrader.webCrawler;

import lombok.extern.slf4j.Slf4j;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@Slf4j
public class SaxErrorHandler implements ErrorHandler {
    @Override
    public void warning(SAXParseException exception) throws SAXException {
      log.warn(exception.getMessage());
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        log.error(exception.getMessage());
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        log.error(exception.getMessage(), exception);
    }
}
