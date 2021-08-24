
package application;

import javafx.scene.Scene;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
public class LottoGui extends Application {
	
	private Id person=new Id();//�մ� ����
	private Lotto lotto=new Lotto();// �ζ� ��ȣ ����

	
	    
	    @Override
	public void start(Stage stage) {
	    	
	    //�ζǹ�ȣ ���� ���� �� �迭
	    lotto.GetAutoNumber(lotto.GetNumbers());
	    lotto.sort(lotto.GetNumbers(), lotto.GetNumbersSize());
	    
	    //����� ��µǴ��� Ȯ���ϱ� ���� test�� consoleâ�� lotto��ȣ �ޱ�
	    for(int i=0;i<7;i++)
	    	System.out.print(lotto.GetNumbers()[i]+" ");
	    
	    MoneyPane moneypane=new MoneyPane();// �����ݾ� ���� GUI
		Scene moneyScene =new Scene(moneypane.getPane(),500,300);
		stage.setTitle("Lotto");
		stage.setScene(moneyScene);
		stage.show();
		
		//���� ������� �Ѿ��
		moneypane.getButton().setOnMouseClicked(e1->{
			if(e1.getButton()==MouseButton.PRIMARY) {
				
				//�Է¹��� �� person��ü�� ����
				person.SetMoney(Integer.parseInt(moneypane.getTF().getText()));
			
				//�����
				//�����ݾ��� 5000���� ���� ���
				person.ControlMoney1();
				//�����ݾ��� 1õ���� �ȵǴ°��
				person.ControlMoney2();
				
				TicketPane ticketpane=new TicketPane();
				
				Scene ticketScene =new Scene(ticketpane.getPane(),500,350);
				stage.setScene(ticketScene);
				stage.show();	
				
				//��ư ������ ����������� �Ѿ��
				ticketpane.getButton().setOnMouseClicked(e2->{
					if(e1.getButton()==MouseButton.PRIMARY) {
						
						ChoicePane choicepane=new ChoicePane();
						
						person.SetTimes(Integer.parseInt(ticketpane.getTF().getText()));//�մ��� ��� �����ߴ��� ����
						
						//���ܼ���
						person.ControlTimes();// ���� �� ���� �� ���� Ƽ���� ������ �� ���
						
						person.SetMoney(person.GetMoney()-1000*person.GetTimes());//������
						
						BuyLotto[] buy=new BuyLotto[person.GetTimes()];//���� ������� ���� buyLotto ��ü ����
						
						//class �迭 nullpointer error �����ϱ� ���� �ʱ�ȭ
						for(int i=0; i <person.GetTimes(); i++){
							 buy[i] = new BuyLotto();
							 buy[i].ResetTextFiled();
							}
						
						for(int i=0;i<person.GetTimes();i++) {
							buy[i].InputNumber(buy[i].GetLabel(),buy[i].GetTextField(),buy[i].GetHBox());//1�帶�� ���� VBox�� 6���� �ؽ�Ʈ�ʵ�� ��ư �ֱ�
							choicepane.getVBox().getChildren().add(buy[i].GetHBox());
						}
						
						
						
						
						Scene ChoiceScene =new Scene(choicepane.getPane(),500,400);
						stage.setScene(ChoiceScene);
						stage.show();
						
						//random�� ������ �� �� �ڵ����� �Է�����
						for(int i=0;i<person.GetTimes();i++) {
							buy[i].GetAutoNumber(buy[i].GetNumbers());
							buy[i].sort(buy[i].GetNumbers(), buy[i].GetNumbersSize());
							buy[i].ButtonRandom(person, buy);
						}
						
						//next�� ���� ��� pane���� �Ѿ
						choicepane.getButton().setOnMouseClicked(e3->{
							//��� pane
							ResultPane resultpane=new ResultPane();
							
							//Text���� buy�� lottonumber �ϳ��� ���ҿ� ���������Ѵ�.
							for(int i=0;i<person.GetTimes();i++) {
								buy[i].IfTyping();
							}
							
							//���ڰ� 1~45�� �ƴϰų� �ߺ��� ���ڰ� ������� �����
							for(int i=0;i<person.GetTimes();i++) {
								
								if(buy[i].Case1(buy[i].GetNumbers())==false) 
									buy[i].Case1Waring();
							
								if(buy[i].Case2(buy[i].GetNumbers())==false)
									buy[i].Case2Warinig();
		
							}
							
							
							//n���� buy�� �ζǹ�ȣ Text�� �ֱ� ���� String
						
							VBox vbresult= new VBox(15);
							Text[] event=new Text[person.GetTimes()];
							
							for(int i=0;i<person.GetTimes();i++) {
								StringBuilder buildNumbers=new StringBuilder();
								
								for(int j=0;j<6;j++) {
								buildNumbers.append(buy[i].GetNumbers()[j]+"  ");
								}
							
							String nNumbers=String.valueOf(buildNumbers);
			
								event[i] =new Text((i+1)+"��°  "+buy[i].CheckResult(lotto.GetNumbers(),buy[i].GetNumbers())+"  "+nNumbers);
								event[i].setFont(Font.font(12));
								vbresult.getChildren().add(event[i]);
							}
							vbresult.setAlignment(Pos.CENTER);
							resultpane.getPane().setCenter(vbresult);
							
							
							Scene resultScene =new Scene(resultpane.getPane(),500,350);
							stage.setScene(resultScene);
							stage.show();
							
							
						});
						
					}
				});
				
				
				
				
				
			}
		});
	}
	
	
	class MoneyPane extends BorderPane{
		private BorderPane pane= new BorderPane();
		private Label label=new Label("�����ݾ�:");
		private TextField tfMoney=new TextField();
		private Label lb1=new Label("������ �ݾ��� �����ּ���");
		private Label lb0=new Label("(���ڸ� �Է��Ͽ� �ֽʽÿ�)");
		private Label lb2=new Label("1000�̻� 5000����(��)������ �����մϴ�");
		private Button btNext=new Button("Next");
		private HBox hb = new HBox(5);
		private VBox vb=new VBox(30);
	    
	    MoneyPane(){
	    	lb1.setFont(Font.font(15));
	    	lb2.setFont(Font.font(12));
	    	lb2.setFont(Font.font(12));
	    	lb0.setTextFill(Color.RED);
	    	
	    	hb.getChildren().addAll(label,tfMoney,btNext);
	    	hb.setAlignment(Pos.CENTER);
	    	vb.getChildren().addAll(lb1,lb0,lb2);
	    	vb.setAlignment(Pos.TOP_CENTER);
	    	vb.setPadding(new Insets(70,0,0,0));
	    	
	    	//���� �ʰ��ϴ� �ݾ� ���Ž� ���â
	    	btNext.setOnMouseClicked(e1->{
				if(e1.getButton()==MouseButton.PRIMARY) {
	
					person.SetMoney(Integer.parseInt(tfMoney.getText()));//�մ� ���� ���ߴ��� ����
					
					//�����ݾ��� 5000���� ���� ���
					person.ControlMoney1();
					//�����ݾ��� 1õ���� �ȵǴ°��
					person.ControlMoney2();
				}
	    	});
	    	
	    	pane.setTop(vb);
	    	pane.setCenter(hb);
	    }
	    
	    public BorderPane getPane() {
	    	return this.pane;
	    }
	    
	    public Button getButton() {
	    	return this.btNext;
	    }
	    
	    public TextField getTF() {
	    	return this.tfMoney;
	    }
	}
	
	class TicketPane extends BorderPane{
		private BorderPane pane= new BorderPane();
		private Label label1=new Label("���� �����Ͻðڽ��ϱ�?");
		private Label label0=new Label("(���ڸ� �Է��Ͽ� �ֽʽÿ�)");
		private Label label2=new Label("�����ݾ�:"+person.GetMoney()+"��");
		private Label label3=new Label("�ζ� 1��� 1000(��) �Դϴ�.");
		private VBox vb=new VBox(10);
		
		private Label lbticket=new Label("������ �� ��:");
		private TextField tfticket=new TextField();
		private Button btNext=new Button("Next");
		private HBox hbticket = new HBox(5);
		
		TicketPane(){
			label1.setFont(Font.font(20));
			label0.setFont(Font.font(15));
			label2.setFont(Font.font(15));
			label3.setFont(Font.font(15));
			label0.setTextFill(Color.RED);
			
			vb.getChildren().addAll(label1,label0,label2,label3);
			vb.setAlignment(Pos.TOP_CENTER);
			vb.setPadding(new Insets(40,0,0,0));
			hbticket.getChildren().addAll(lbticket,tfticket,btNext);
			hbticket.setAlignment(Pos.CENTER);
			
			pane.setTop(vb);
			pane.setCenter(hbticket);
			
		}
		
		public BorderPane getPane() {
			return this.pane;
		}
		public Button getButton() {
			return this.btNext;
		}
		public TextField getTF() {
			return this.tfticket;
		}
		
	}
	
	class ChoicePane extends BorderPane{
		private BorderPane pane=new BorderPane();
		private VBox vb=new VBox(40);
		private Button btNext=new Button("Next");
		
		ChoicePane(){		
		}
		
		public BorderPane getPane() {
			vb.getChildren().add(btNext);
			vb.setPadding(new Insets(40,60,40,60));
			pane.setCenter(vb);
			return this.pane;
		}
		public Button getButton() {
			return this.btNext;
		}
		public VBox getVBox() {
			return this.vb;
		}
	}
	
	class ResultPane extends ChoicePane{
		BorderPane pane=new BorderPane();
		
		VBox vbresult=new VBox(15);
		
		ResultPane(){
			
			
			//n�� �ζ� ��ȣ
			StringBuilder numbers=new StringBuilder();
			for(int i=0;i<lotto.GetNumbersSize()-1;i++) {
				numbers.append(lotto.GetNumbers()[i]+"  ");
			}
			//���ʽ���ȣ
			String bonusNumber=String.valueOf(lotto.GetNumbers()[6]);
			
			Label label1=new Label("n�� �ζ� ��ȣ�� ������ �����ϴ�.");
			Text txResultNumber=new Text("n�� �ζǹ�ȣ: "+ numbers);
			Text txBonus=new Text("���ʽ� ��ȣ:  "+bonusNumber);
			
			label1.setFont(Font.font(20));
			txResultNumber.setFont(Font.font(15));
			txResultNumber.setFill(Color.GREEN);
			txBonus.setFont(Font.font(15));
			txBonus.setFill(Color.RED);
			
			Text leftMoney=new Text("���� �ݾ�: "+person.GetMoney()+"��");
			vbresult.getChildren().addAll(label1,txResultNumber, txBonus);
			vbresult.setAlignment(Pos.CENTER);
			
			pane.setTop(vbresult);
			pane.setBottom(leftMoney);
			
		}
		
		
		public BorderPane getPane() {

			return pane;
			
		}
	}
	
	public static void main(String[] args) {
		launch(args);

	}

}
