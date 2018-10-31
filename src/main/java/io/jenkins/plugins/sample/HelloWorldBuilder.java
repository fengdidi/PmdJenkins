package io.jenkins.plugins.sample;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class HelloWorldBuilder extends Builder implements SimpleBuildStep {

    private final String reportPath;
    private final String notesAddr;
    private final String pmdCMD;
    private final String projectPath;
    private final String pmdReportName;
    public String command;

    @DataBoundConstructor
    public HelloWorldBuilder(String pmdCMD,String projectPath,String reportPath,String notesAddr,String pmdReportName) {
        this.pmdCMD = pmdCMD;
        this.projectPath = projectPath;
        this.reportPath = reportPath;
        this.notesAddr = notesAddr;
        this.pmdReportName = pmdReportName;
    }

    @Override
    public void perform(Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        String	m_dominoServer 		= getDescriptor().getDominoServer();
        String	m_dominoMailbox 	= getDescriptor().getDominoMailbox();
        String	m_dominoUsername 	= getDescriptor().getDominoUsername();
        String	m_dominoPassword 	= getDescriptor().getDominoPassword();
        boolean useProxy = getDescriptor().getUseProxy();
        String proxyUrl = getDescriptor().getProxyUrl();
        String m_dominoPmd = getDescriptor().getDominoPmd();
        command = "cmd /c e: & cd "+m_dominoPmd+ "& pmd -d "+projectPath + reportPath + pmdReportName + pmdCMD;
        listener.getLogger().println("PMDcmd:"+ pmdCMD);
        listener.getLogger().println("m_ReportPath:"+ notesAddr);
        listener.getLogger().println("m_PmdCmd:"+ reportPath);
        listener.getLogger().println("m_dominoServer:"+m_dominoServer);
        listener.getLogger().println("m_dominoMailbox:"+m_dominoMailbox);
        listener.getLogger().println("m_dominoUsername:"+m_dominoUsername);
        listener.getLogger().println("m_dominoPassword:"+m_dominoPassword);
        listener.getLogger().println("useProxy:"+useProxy);
        listener.getLogger().println("proxyUrl:"+proxyUrl);
        listener.getLogger().println("m_dominoPmd:"+reportPath);
        exeCmd(command);
        File reportDir = new File(reportPath);
        File[] listOfFiles = reportDir.listFiles();
        List<File> unitTestReport = new LinkedList<File>();
        for(int i = 0; i < listOfFiles.length; i++) {
            String filename = listOfFiles[i].getName();
            if (filename.endsWith(".xml") || filename.endsWith(".XML")) {
                listener.getLogger().println("识别到PMD测试报告文件："+filename);
                unitTestReport.add(listOfFiles[i]);
            }
        }


    }
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }
    public static void exeCmd(String commandStr) {
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            if (br != null)
            {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    private String parsePMDReports(List<File> reports){
//        DocumentBuilderFactory a= DocumentBuilderFactory.newInstance();
//        int priority1 =0;
//        int priority2 =0;
//        int priority3 =0;
//        int priority4 =0;
//        int priority5 =0;
//        try{
//            DocumentBuilder b = a.newDocumentBuilder();
//            Document document = b.parse("D:\\pmd-report-test.xml");
//            NodeList booklist1 = document.getElementsByTagName("file");
//            NodeList booklist = document.getElementsByTagName("violation");
//            //System.out.println("一共有" + booklist1.getLength() + "个文件检测出问题;" + "一共有" + booklist.getLength() + "个问题.");
//            System.out.println("============PMD Report=============");
//            for(int i =0; i<booklist1.getLength(); i++) {
//                Node book = booklist1.item(i);
//                NamedNodeMap bookmap = book.getAttributes();
//            }
//
//            for(int i =0; i<booklist.getLength(); i++) {
//
//                //循环遍历获取每一个book
//                Node book = booklist.item(i);
//                //通过Node对象的getAttributes()方法获取全的属性值
//                NamedNodeMap bookmap = book.getAttributes();
//                //循环遍每一个book的属性值
//                for (int j = 0; j < bookmap.getLength(); j++) {
//                    Node node = bookmap.item(j);
//                    //通过Node对象的getNodeName()和getNodeValue()方法获取属性名和属性值
//                    if (node.getNodeName().equals("priority")&&node.getNodeValue().equals("1")){
//                        priority1+=1;
//                    }
//                    if (node.getNodeName().equals("priority")&&node.getNodeValue().equals("2")){
//                        priority2+=1;
//                    }
//                    if (node.getNodeName().equals("priority")&&node.getNodeValue().equals("3")){
//                        priority3+=1;
//                    }
//                    if (node.getNodeName().equals("priority")&&node.getNodeValue().equals("4")){
//                        priority4+=1;
//                    }
//                    if (node.getNodeName().equals("priority")&&node.getNodeValue().equals("5")){
//                        priority5+=1;
//                    }
//                }
//                NodeList childlist = book.getChildNodes();
//            }
////            String report = "一共有" + booklist1.getLength() + "个文件检测出问题;" + "一共有" + booklist.getLength() + "个问题.\n"+"严重且优先高的错误共有： " + priority1+"个\n"
////                    + "严重且优先正常的错误共有： " + priority2+"个\n"
////                    +"严重且优先高的警告共有：" + priority3+"个\n"
////                    +"严重且优先正常的警告共有： " + priority4+"个\n"
////                    +"严重且优先正常的信息共有： " + priority5+"个\n"
////                    +"具体问题请到Jenkins工作流中查询！";
//            System.out.println("一共有" + booklist1.getLength() + "个文件检测出问题;" + "一共有" + booklist.getLength() + "个问题.");
//            System.out.println("严重且优先高的错误共有： " + priority1+"个");
//            System.out.println("严重且优先正常的错误共有： " + priority2+"个");
//            System.out.println("严重且优先高的警告共有：" + priority3+"个");
//            System.out.println("严重且优先正常的警告共有： " + priority4+"个");
//            System.out.println("严重且优先正常的信息共有： " + priority5+"个");
//            System.out.println("具体问题请到Jenkins工作流中查询！");
//            for(int i =0; i<booklist1.getLength(); i++) {
//
//                //循环遍历获取每一个book
//                Node book = booklist1.item(i);
//                //通过Node对象的getAttributes()方法获取全的属性值
//                NamedNodeMap bookmap = book.getAttributes();
//                //循环遍每一个book的属性值
//                for (int j = 0; j < bookmap.getLength(); j++) {
//                    Node node = bookmap.item(j);
//                    //System.out.print(node.getNodeName() + ":");
//                    //System.out.println(node.getNodeValue());
//                }
//                //解析book节点的子节点
//                NodeList childlist = book.getChildNodes();
//                for (int t = 0; t < childlist.getLength(); t++) {
//                    //区分出text类型的node以及element类型的node
//                    if (childlist.item(t).getNodeType() == Node.ELEMENT_NODE) {
//                        System.out.print(childlist.item(t).getNodeName() + ":"+ childlist.item(t).getTextContent());
//                        //System.out.println(childlist.item(t).getTextContent());
//                    }
//                }
//            }
//        }catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private String dominoServer;
        private String dominoMailbox;
        private String dominoUsername;
        private String dominoPassword;
        private String dominoPmd;
        private boolean useProxy;
        private String proxyUrl;

        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }


        public FormValidation doCheckReportPath(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("请输入单元测试报告路径");
            return FormValidation.ok();
        }

        public FormValidation doCheckNotesAddr(@QueryParameter String value)
                throws IOException, ServletException {
            return FormValidation.ok();
        }


        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "NOTES发送单元测试报告（Surefire）";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            dominoServer = formData.getString("dominoServer");
            dominoMailbox = formData.getString("dominoMailbox");
            dominoUsername = formData.getString("dominoUsername");
            dominoPassword = formData.getString("dominoPassword");
            dominoPmd = formData.getString("dominoPmd");
            useProxy = formData.getBoolean("useProxy");
            proxyUrl = formData.getString("proxyUrl");

            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req,formData);
        }

        /**
         * This method returns true if the global configuration says we should speak French.
         *
         * The method name is bit awkward because global.jelly calls this method to determine
         * the initial state of the checkbox by the naming convention.
         */

        public String getDominoServer() {
            return dominoServer;
        }

        public String getDominoMailbox() {
            return dominoMailbox;
        }

        public String getDominoUsername() {
            return dominoUsername;
        }

        public String getDominoPassword() {
            return dominoPassword;
        }

        public boolean getUseProxy() {
            return useProxy;
        }

        public String getProxyUrl() {
            return proxyUrl;
        }

        public String getDominoPmd(){ return dominoPmd;}

    }
}
