package org.interview.client;

import org.interview.shared.FieldVerifier;
import org.interview.shared.PersonInfo;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Hello implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/*
	 * UI WT 
	 */
	final Button sendButton = new Button("Send");
    final TextBox nameField = new TextBox();
    final Label errorLabel = new Label();
    final DialogBox dialogBox = new DialogBox();
    final Button closeButton = new Button("Close");
    final Label textToServerLabel = new Label();
    final HTML serverResponseLabel = new HTML();
    VerticalPanel dialogVPanel = new VerticalPanel();
    
    Integer numberOfCompletedCall = new Integer(0);
    final Label countCallLabel = new Label();
    final Button countCallButton = new Button("Count Calls");
	
    
    /*
     * UI for database calls
     * */
    final Button getPersonButton = new Button("Get person information");
    final Button clearPersonButton = new Button("Clear person information");
    
    final Label personLabelName = new Label("Name");
    final Label personLabelId = new Label("Id");

    final TextBox personName = new TextBox();
    final TextBox personId = new TextBox();
    
    DecoratorPanel personPanel = new DecoratorPanel();
    
    
    /**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	
	/**
	 * 
	 */
	private final PersonServiceAsync personService = GWT.create(PersonService.class);
	
	/**
	 * Create a remote service proxy to talk to the server-side Count call service
	 */
	private  final CountCallServiceAsync countCallService = GWT.create(CountCallService.class);
	 

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
	nameField.setText("Gwt User");
        // We can add style names to widgets
        sendButton.addStyleName("sendButton");
        //git diff":Change the color of the Send button by adding the CSS class red 
        sendButton.addStyleName("red");
        
        // Focus the cursor on the name field when the app loads
        nameField.setFocus(true);
        nameField.selectAll();

        // Create the popup dialog box
        dialogBox.setText("Remote Procedure Call");
        dialogBox.setAnimationEnabled(true);
        
        // We can set the id of a widget by accessing its Element
        closeButton.getElement().setId("closeButton");
        dialogVPanel.addStyleName("dialogVPanel");
        dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
        dialogVPanel.add(textToServerLabel);
        dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
        dialogVPanel.add(serverResponseLabel);
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
        dialogVPanel.add(closeButton);
        dialogBox.setWidget(dialogVPanel);
        
        // Add a handler to close the DialogBox
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                sendButton.setEnabled(true);
                sendButton.setFocus(true);
            }
        });
        
        //git diff: Use the countCallButton as the action button and countCallLabel to show the number of succeeded calls
        countCallButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                countCallLabel.setText(numberOfCompletedCall);

            }
        });
        
        //git diff: Add the handler on the button 'Clear person information' the clear the person's text box.
        clearPersonButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	personName.setText("");
            	personId.setText("");
                
            }
        });
        

        // Create a handler for the sendButton and nameField
        class MyHandler implements ClickHandler, KeyUpHandler {
            /**
             * Fired when the user clicks on the sendButton.
             */
            public void onClick(ClickEvent event) {
                sendNameToServer();
            }

            /**
             * Fired when the user types in the nameField.
             */
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    sendNameToServer();
                }
            }

            /**
             * Send the name from the nameField to the server and wait for a response.
             */
            private void sendNameToServer() {
                // First, we validate the input.
                errorLabel.setText("");
                String textToServer = nameField.getText();
                if (!FieldVerifier.isValidName(textToServer)) {
                	//git diff:Change the error message when sending a name with least than 4 characters with the Send button	
                    errorLabel.setText("Please enter more than or equal to 4 caracters");
                    return;
                }

                // Then, we send the input to the server.
                sendButton.setEnabled(false);
                textToServerLabel.setText(textToServer);
                serverResponseLabel.setText("");
                
                // Async call to greet server
                greetingService.greetServer(textToServer,new AsyncCallback<String>() {
                            public void onFailure(Throwable caught) {
                                // Show the RPC error message to the user
                                dialogBox.setText("Remote Procedure Call - Failure");
                                serverResponseLabel.addStyleName("serverResponseLabelError");
                                serverResponseLabel.setHTML(SERVER_ERROR);
                                dialogBox.center();
                                closeButton.setFocus(true);
                            }

                            public void onSuccess(String result) {
                                dialogBox.setText("Remote Procedure Call");
                                serverResponseLabel.removeStyleName("serverResponseLabelError");
                                serverResponseLabel.setHTML(result);
                                dialogBox.center();
                                closeButton.setFocus(true);

                            }
                        });
                //git diff:Use the CountCallService to add and show number of call message successfully sent to the server
                countCallService.countCall(numberOfCompletedCall, new AsyncCallback<Integer>() {
   			public void onSuccess(Integer result) {
     				numberOfCompletedCall=result;
     				dialogBox.setText("Call message successfully sent to the server");
     				dialogBox.center();
     				countCallButton.setFocus(true);
   			}
 
   			public void onFailure(Throwable caught) {
			// Show the RPC error message to the user
                                dialogBox.setText("Cannot get number of call message successfully sent to the server");
                                dialogBox.center();
                                countCallButton.setFocus(true);
			}
		});
            }
        }

        // Add a handler to send the name to the server
        MyHandler handler = new MyHandler();
        sendButton.addClickHandler(handler);
        nameField.addKeyUpHandler(handler);

        /* Layout for person information*/
        FlexTable layout = new FlexTable();
        layout.setCellSpacing(6);
        FlexCellFormatter cellFormatter = layout.getFlexCellFormatter();
        layout.setHTML(0, 0, "Person information");
        cellFormatter.setColSpan(0, 0, 2);
        cellFormatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
        layout.setWidget(1, 0, personLabelId);
        layout.setWidget(1, 1, personId);
        layout.setWidget(2, 0, personLabelName);
        layout.setWidget(2, 1, personName);
        layout.setWidget(3, 0, getPersonButton);
        layout.setWidget(3, 1, clearPersonButton);
        personPanel.setWidget(layout);

        setupPersonSection();
        
        RootPanel.get("personContainer").add(personPanel);
		
		RootPanel.get("nameFieldContainer").add(nameField);
        RootPanel.get("sendButtonContainer").add(sendButton);
        RootPanel.get("sendButtonContainer").add(countCallButton);
        RootPanel.get("countCall").add(countCallLabel);
        RootPanel.get("errorLabelContainer").add(errorLabel);
	}
	
	
	/**
	 * Section to implement for person database calls
	 */
	public void setupPersonSection(){
	    
	    getPersonButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onGetPersonClick();
            }
        });
	    
	}
	
	
	private void onGetPersonClick(){
	    int personId = 1;
	    
	    personService.getPerson(personId, new AsyncCallback<PersonInfo>() {
	        @Override
	        public void onFailure(Throwable caught) {
	            caught.printStackTrace();
	            Window.alert("Error : " + caught.getMessage());
	        }
	        public void onSuccess(PersonInfo result) {
	        	updatePersonInformation(result);
	        };
	    });
	}
	
	private void updatePersonInformation(PersonInfo person){
		personId.setText(String.valueOf(person.getPersonID()));
		personName.setText(person.getName());
	}
	
}
