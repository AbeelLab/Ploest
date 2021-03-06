package abeellab.pedca;



import jMEF.MixtureModel;
import jMEF.PVector;;



public class NaivePDF {
	double [] yDataPoints;
	double [] xDataPoints;


	float[] readCounts;//normalized read counts

	double beg;//bgining of x axis
	double end;//end of x axis
	double step=1;//step on x axis
    double maxYFITvalue=0.0;
    double maxYHISTOGRAMvalue=0.0;
	double yRatioCorrection=0.0;//to correct the y data normalization ratio

    double maxXvalue=0.0;
    int peakYvalueIndex=0;//index f the peak value (therefore in both histogram and fit). Used to find the correct ratio of both plots
    MixtureModel mixtMod;
    static int smootherLength;//bin width (values are averaged within each bin)
    static int smootherWing;
    
	public NaivePDF(float[] rc){
		
		readCounts=rc;
		maxXvalue=readCounts.length;
		
		smootherLength=(int) (maxXvalue/(Math.round(Pedca.BIN_FACTOR*NaivePedcaPlotter.MAX_PLOIDY)));//the window of our smoother must be able to discretize over at least 10 different clusters (MAX_NB_MIXTURES). The minimum length should be 2X. We go for a safer 3X
		System.out.println("smootherLength:"+smootherLength+ " maxXvalue:"+maxXvalue+" Pedca.BIN_FACTOR:"+Pedca.BIN_FACTOR+" NaivePedcaPlotter.MAX_PLOIDY:"+NaivePedcaPlotter.MAX_PLOIDY);
		if((smootherLength & 1) == 0   ){//if even number
			smootherLength++;//must be odd number
		}
		smootherWing=smootherLength/2;//part of the smootherLength before or after the pointer
		
		yDataPoints=new double[readCounts.length];
		xDataPoints=new double[readCounts.length];
		

		for (int p=0;p<readCounts.length;p++){//for each point
			double sum=0;
			int substract=0;//substract these bins (for begining and end of genome)
			
			for(int cp=(p-smootherWing);cp<(p+smootherWing);cp++){//sum over the smoother window
				
				if(cp>0 && cp<readCounts.length){
					sum+=readCounts[cp];
				}else {
					substract++;	//but substract these points if the wind goes out of the existing range
				}
			}
			double average=sum/(smootherLength-substract);
			
			for(int cp=(p-smootherWing);cp<(p+smootherWing);cp++){//set the average value over the smoothing window	
				if(cp>0 && cp<readCounts.length){
					xDataPoints[cp]=(double)cp;
					yDataPoints[cp]=average;//remove points that were outside of range
					if(yDataPoints[cp]>maxYFITvalue){
						maxYFITvalue=yDataPoints[cp];
					}
				}
			}
			p=p+smootherWing-1;//move p to next bin start point
		}
	}
	
	

}

