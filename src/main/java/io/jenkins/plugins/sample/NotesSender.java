package io.jenkins.plugins.sample;

import lotus.domino.*;

import java.util.Vector;

public class NotesSender {

    private String dominoServer;
    private String dominoMailbox;
    private String dominoUsername;
    private String dominoPassword;

    public NotesSender(String dominoServer, String dominoMailbox, String dominoUsername, String dominoPassword ){
        this.dominoServer = dominoServer;
        this.dominoMailbox = dominoMailbox;
        this.dominoUsername = dominoUsername;
        this.dominoPassword = dominoPassword;
    }

    public void send( Vector sendTo, Vector copyTo, Vector blindCopyTo,String subject, String message, Vector attachment ) throws
            Exception {
        try {
            lotus.domino.Session dominoSession = lotus.domino.NotesFactory.createSession( dominoServer, dominoUsername, dominoPassword );
            lotus.domino.Database dominoDb = dominoSession.getDatabase(null, dominoMailbox );
            lotus.domino.Document memo = dominoDb.createDocument();
            memo.appendItemValue( "Form", "Memo" );
            memo.appendItemValue( "Importance", "1" );
            memo.appendItemValue( "CopyTo", copyTo );
            memo.appendItemValue( "blindCopyTo", blindCopyTo );
            memo.appendItemValue( "Subject", subject );

            // 以下是两种文本内容的方式，第一种发纯文本，第二种可以识别html
            //memo.appendItemValue("Body", message);

            RichTextItem body = memo.createRichTextItem( "Body" );
            RichTextStyle header = dominoSession.createRichTextStyle();
            //header.setBold(RichTextStyle.YES);
            //header.setColor(RichTextStyle.COLOR_DARK_BLUE);
            //header.setEffects(RichTextStyle.EFFECTS_SHADOW);
            //header.setFont(RichTextStyle.FONT_ROMAN);
            header.setFontSize( 14 );
            header.setPassThruHTML( RichTextStyle.YES );
            body.appendStyle( header );
            body.appendText( message );
            body.addNewLine();

            for( int i = 0; i < attachment.size(); i++ ) {
                String att = ( String )attachment.get( i );
                body.embedObject( EmbeddedObject.EMBED_ATTACHMENT, "", att, "" );
                body.addNewLine();
            }

            // 发送邮件
            memo.send( false, sendTo );
            //Recycle
            dominoDb.recycle();
            dominoSession.recycle();
        }
        catch( NotesException e ) {
            e.printStackTrace( System.out );
            //throw new EAMSException(e.getMessage());
            throw new Exception(e.getMessage());
        }
        catch( Exception e ) {
            e.printStackTrace( System.out );
            //throw new EAMSException(e.getMessage());
            throw new Exception(e.getMessage());
        }

    }
}
