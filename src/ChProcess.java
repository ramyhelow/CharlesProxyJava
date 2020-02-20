import org.w3c.dom.*;
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

    static File processesFile;
    static DocumentBuilderFactory icFactory;
    static DocumentBuilder icBuilder;
    static Document doc;

    static ChProcess chProcess;

    public ChProcess() throws ParserConfigurationException, TransformerException, SAXException, IOException {
        init("");
    }

    public static ChProcess getInstance() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        if(chProcess==null) {
            chProcess = new ChProcess();
        }
            return chProcess;

    }

    public static void init(String fileDirectory) throws IOException, ParserConfigurationException, SAXException, TransformerException {

        icFactory = DocumentBuilderFactory.newInstance();
        icBuilder = icFactory.newDocumentBuilder();
        processesFile = new File(fileDirectory+"processes.xml");
        doc = icBuilder.newDocument();

        //new File(fileDirectory).mkdirs();
        boolean fileExists = processesFile.createNewFile();

        if (!fileExists) {
            System.out.println("Processes File already exists.");
            doc = icBuilder.parse(processesFile);
        } else {
            System.out.println("Processes File has been created.");
            Element mainRootElement = doc.createElement("processes");
            doc.appendChild(mainRootElement);
            writeDomToFile(doc);
        }
    }

    public static void startProcess(int devicePortNumber, String configFilePath) throws IOException, TransformerException, ParserConfigurationException, SAXException {
        killProcess(devicePortNumber);
        Process myProcess = new ProcessBuilder("Charles.exe", "-headless -config "+configFilePath).start();
        addProcessToFile(Integer.toString(devicePortNumber), Long.toString(myProcess.pid()));
    }

    public static void killProcess(int devicePortNumber) throws IOException, TransformerException, ParserConfigurationException, SAXException {
        int pid = getAndRemoveProcessFromFile(devicePortNumber);
        System.out.println("PID: " + pid);
        if (pid != 0) {
            Runtime.getRuntime().exec("taskkill /F /PID " + pid);
        }
    }


    private static void addProcessToFile(String portNumber, String pid) throws IOException, ParserConfigurationException, TransformerException, SAXException {
        doc.getDocumentElement().appendChild(createDeviceNode(doc, portNumber, pid));
        writeDomToFile(doc);
    }


    public static int getAndRemoveProcessFromFile(int deviceNumber) throws IOException, ParserConfigurationException, SAXException, TransformerException {

        NodeList nList = doc.getElementsByTagName("device");

        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            //System.out.println("\nCurrent Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                //System.out.println(eElement.getElementsByTagName("port").item(0).getTextContent());
                if (Integer.parseInt(eElement.getElementsByTagName("port").item(0).getTextContent())==(deviceNumber)) {
                    int pid = Integer.parseInt(eElement.getElementsByTagName("pid").item(0).getTextContent());
                    //System.out.println(pid);
                    eElement.getParentNode().removeChild(eElement);
                    writeDomToFile(doc);
                    return pid;
                }
            }
        }

        return 0;

    }

    public static void killAllCharlesInstances() throws IOException {
        Runtime.getRuntime().exec("taskkill /IM \"Charles.exe\" /F");
    }

//    public static int getProcessNumberFromFile(int deviceNumber) throws IOException, ParserConfigurationException, SAXException {
//
//
//        boolean newFileCreated = !processesFile.createNewFile();
//
//        if (newFileCreated) {
//            return 0;
//        } else {
//            icBuilder = icFactory.newDocumentBuilder();
//            Document doc = icBuilder.parse(processesFile);
//
//            NodeList nList = doc.getElementsByTagName("device");
//
//            for (int temp = 0; temp < nList.getLength(); temp++) {
//                Node nNode = nList.item(temp);
//                //System.out.println("\nCurrent Element :" + nNode.getNodeName());
//                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//                    Element eElement = (Element) nNode;
//                    if (eElement.getElementsByTagName("port").item(0).getTextContent().equals(deviceNumber)) {
//                        System.out.println("Found");
//                        return Integer.parseInt(eElement.getElementsByTagName("pid").item(0).getTextContent());
//                    }
//                }
//            }
//
//            return 0;
//        }
//    }

    public static void writeDomToFile(Document doc) throws TransformerException {
        doc.getDocumentElement().normalize();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource domSource = new DOMSource(doc);
        StreamResult result = new StreamResult("processes.xml");
        transformer.transform(domSource, result);
    }

    private static Node createDeviceNode(Document doc, String port, String pid) {
        Element device = doc.createElement("device");
        device.appendChild(createDeviceNodeElements(doc, device, "port", port));
        device.appendChild(createDeviceNodeElements(doc, device, "pid", pid));
        return device;
    }

    private static Node createDeviceNodeElements(Document doc, Element element, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }
}
