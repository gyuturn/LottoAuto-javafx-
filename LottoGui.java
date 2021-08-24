
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
	
	private Id person=new Id();//손님 입장
	private Lotto lotto=new Lotto();// 로또 번호 생성

	
	    
	    @Override
	public void start(Stage stage) {
	    	
	    //로또번호 임의 생성 및 배열
	    lotto.GetAutoNumber(lotto.GetNumbers());
	    lotto.sort(lotto.GetNumbers(), lotto.GetNumbersSize());
	    
	    //제대로 출력되는지 확인하기 위해 test로 console창에 lotto번호 받기
	    for(int i=0;i<7;i++)
	    	System.out.print(lotto.GetNumbers()[i]+" ");
	    
	    MoneyPane moneypane=new MoneyPane();// 충전금액 묻는 GUI
		Scene moneyScene =new Scene(moneypane.getPane(),500,300);
		stage.setTitle("Lotto");
		stage.setScene(moneyScene);
		stage.show();
		
		//다음 장면으로 넘어가기
		moneypane.getButton().setOnMouseClicked(e1->{
			if(e1.getButton()==MouseButton.PRIMARY) {
				
				//입력받은 돈 person객체에 저장
				person.SetMoney(Integer.parseInt(moneypane.getTF().getText()));
			
				//경고문구
				//충전금액이 5000원이 넘을 경우
				person.ControlMoney1();
				//충전금액이 1천원이 안되는경우
				person.ControlMoney2();
				
				TicketPane ticketpane=new TicketPane();
				
				Scene ticketScene =new Scene(ticketpane.getPane(),500,350);
				stage.setScene(ticketScene);
				stage.show();	
				
				//버튼 누르면 다음장면으로 넘어가기
				ticketpane.getButton().setOnMouseClicked(e2->{
					if(e1.getButton()==MouseButton.PRIMARY) {
						
						ChoicePane choicepane=new ChoicePane();
						
						person.SetTimes(Integer.parseInt(ticketpane.getTF().getText()));//손님이 몇번 구매했는지 저장
						
						//예외설정
						person.ControlTimes();// 보유 돈 보다 더 많은 티켓을 구매할 시 경고
						
						person.SetMoney(person.GetMoney()-1000*person.GetTimes());//남은돈
						
						BuyLotto[] buy=new BuyLotto[person.GetTimes()];//몇장 샀는지에 따라 buyLotto 객체 생성
						
						//class 배열 nullpointer error 예방하기 위해 초기화
						for(int i=0; i <person.GetTimes(); i++){
							 buy[i] = new BuyLotto();
							 buy[i].ResetTextFiled();
							}
						
						for(int i=0;i<person.GetTimes();i++) {
							buy[i].InputNumber(buy[i].GetLabel(),buy[i].GetTextField(),buy[i].GetHBox());//1장마다 각각 VBox에 6개의 텍스트필드와 버튼 넣기
							choicepane.getVBox().getChildren().add(buy[i].GetHBox());
						}
						
						
						
						
						Scene ChoiceScene =new Scene(choicepane.getPane(),500,400);
						stage.setScene(ChoiceScene);
						stage.show();
						
						//random을 누르게 될 시 자동으로 입력해줌
						for(int i=0;i<person.GetTimes();i++) {
							buy[i].GetAutoNumber(buy[i].GetNumbers());
							buy[i].sort(buy[i].GetNumbers(), buy[i].GetNumbersSize());
							buy[i].ButtonRandom(person, buy);
						}
						
						//next를 눌려 결과 pane으로 넘어감
						choicepane.getButton().setOnMouseClicked(e3->{
							//결과 pane
							ResultPane resultpane=new ResultPane();
							
							//Text값을 buy의 lottonumber 하나의 원소에 각각대입한다.
							for(int i=0;i<person.GetTimes();i++) {
								buy[i].IfTyping();
							}
							
							//숫자가 1~45가 아니거나 중복된 숫자가 잇을경우 경고문구
							for(int i=0;i<person.GetTimes();i++) {
								
								if(buy[i].Case1(buy[i].GetNumbers())==false) 
									buy[i].Case1Waring();
							
								if(buy[i].Case2(buy[i].GetNumbers())==false)
									buy[i].Case2Warinig();
		
							}
							
							
							//n번쨰 buy한 로또번호 Text에 넣기 위해 String
						
							VBox vbresult= new VBox(15);
							Text[] event=new Text[person.GetTimes()];
							
							for(int i=0;i<person.GetTimes();i++) {
								StringBuilder buildNumbers=new StringBuilder();
								
								for(int j=0;j<6;j++) {
								buildNumbers.append(buy[i].GetNumbers()[j]+"  ");
								}
							
							String nNumbers=String.valueOf(buildNumbers);
			
								event[i] =new Text((i+1)+"번째  "+buy[i].CheckResult(lotto.GetNumbers(),buy[i].GetNumbers())+"  "+nNumbers);
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
		private Label label=new Label("충전금액:");
		private TextField tfMoney=new TextField();
		private Label lb1=new Label("충전할 금액을 적어주세요");
		private Label lb0=new Label("(숫자만 입력하여 주십시오)");
		private Label lb2=new Label("1000이상 5000이하(원)까지만 가능합니다");
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
	    	
	    	//범위 초과하는 금액 구매시 경고창
	    	btNext.setOnMouseClicked(e1->{
				if(e1.getButton()==MouseButton.PRIMARY) {
	
					person.SetMoney(Integer.parseInt(tfMoney.getText()));//손님 충전 얼마했는지 저장
					
					//충전금액이 5000원이 넘을 경우
					person.ControlMoney1();
					//충전금액이 1천원이 안되는경우
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
		private Label label1=new Label("몇장 구매하시겠습니까?");
		private Label label0=new Label("(숫자만 입력하여 주십시오)");
		private Label label2=new Label("충전금액:"+person.GetMoney()+"원");
		private Label label3=new Label("로또 1장당 1000(원) 입니다.");
		private VBox vb=new VBox(10);
		
		private Label lbticket=new Label("구매할 장 수:");
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
			
			
			//n차 로또 번호
			StringBuilder numbers=new StringBuilder();
			for(int i=0;i<lotto.GetNumbersSize()-1;i++) {
				numbers.append(lotto.GetNumbers()[i]+"  ");
			}
			//보너스번호
			String bonusNumber=String.valueOf(lotto.GetNumbers()[6]);
			
			Label label1=new Label("n차 로또 번호는 다음과 같습니다.");
			Text txResultNumber=new Text("n차 로또번호: "+ numbers);
			Text txBonus=new Text("보너스 번호:  "+bonusNumber);
			
			label1.setFont(Font.font(20));
			txResultNumber.setFont(Font.font(15));
			txResultNumber.setFill(Color.GREEN);
			txBonus.setFont(Font.font(15));
			txBonus.setFill(Color.RED);
			
			Text leftMoney=new Text("남은 금액: "+person.GetMoney()+"원");
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
