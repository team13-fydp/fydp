
public class testMatrix {
	
	public static void main(String[] args) {
		
		int [][] availableTime = new int[11][30];
		
		for(int i= 0; i<=8;i++) {
			for (int j=0;j<=29;j++) {
				availableTime[i][j] =1;
				System.out.println(availableTime[i][j]);
			}
			
		}

	}
	
	
}
