import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class ChProcess {

    public static void startProcess(int devicePortNumber, String newSpeed, String configFilePath) throws IOException {
        Process myProcess = new ProcessBuilder("Charles.exe", "-headless -config " + configFilePath).start();
        long pid = myProcess.pid();
    }

    public static void killProcess(int devicePortNumber) throws IOException {
        int pid = getProcessNumber(devicePortNumber);
        Process myProcess = new ProcessBuilder("taskkill /F /PID " + pid).start();
    }

    private static int getProcessNumber(int devicePortNumber) throws IOException {
        addProcessToFile();
        return 0;
    }

//    public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException {
//        mapProcess();
//    }

    private static void addProcessToFile(String portNumber, String pid) throws IOException, ParserConfigurationException, TransformerException, SAXException {
        File processesFile = new File("processes.xml");

        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder = icFactory.newDocumentBuilder();;
        Document doc;

        //if file doesn't exist
        if (processesFile.createNewFile()) {
            System.out.println("Processes File has been created.");
            doc = icBuilder.newDocument();
            Element mainRootElement = doc.createElement("Processes");
            doc.appendChild(mainRootElement);
            System.out.println("\nXML DOM Created Successfully..");

            ////if file already exists
        } else {
            System.out.println("Processes File already exists.");
            doc = icBuilder.parse(processesFile);
            Element mainRootElement = doc.getDocumentElement();
            mainRootElement.appendChild(getDevice(doc, portNumber, pid));
        }

        writeDomToFile(doc);
    }

    public boolean removeProcessFromFile(String processNumber) throws IOException, ParserConfigurationException, SAXException {
        File processesFile = new File("processes.xml");
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;

        if (processesFile.createNewFile()) {
            return false;
        }else{
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.parse(processesFile);


        }
    }

    public void getProcessNumberFromFile(){

    }

    public static void writeDomToFile(Document doc) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult console = new StreamResult("processes.xml");
        transformer.transform(source, console);
    }

    private static Node getDevice(Document doc, String port, String pid) {
        Element device = doc.createElement("Device");
        device.appendChild(getDeviceElements(doc, device, "port", port));
        device.appendChild(getDeviceElements(doc, device, "pid", pid));
        return device;
    }

    private static Node getDeviceElements(Document doc, Element element, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }
}
