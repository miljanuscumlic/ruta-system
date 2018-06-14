package rs.ruta.client;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureTimeoutTest
{

	public static void main(String [] args )
	{
		ExecutorService exec = Executors.newSingleThreadExecutor();
		final Callable<?> call = new Callable<Object>()
		{
			@Override
			public Object call() throws Exception {
				try
				{
					Thread.sleep(2000);
				}
				catch (InterruptedException ex)
				{
					ex.printStackTrace();
				}
				return 0;
			}
		};

		long time1 = System.nanoTime();

	    System.out.println("Submitting");
	    final Future<?> future = exec.submit(call);
	    try
	    {
	        future.get(1000, TimeUnit.MILLISECONDS);

	        long time2 = System.nanoTime();
	        System.out.println("No timeout after " +
	                             (time2-time1)/1000000000.0 + " seconds");

	        System.out.println("failed: expectedTimeoutException");
	    }
	    catch (TimeoutException ignore)
	    {
	        long time2 = System.nanoTime();
	        System.out.println("Timed out after " +
	                             (time2-time1)/1000000000.0 + " seconds");
	    }
		catch (InterruptedException | ExecutionException e)
		{
			e.printStackTrace();
		}
	    finally
	    {
	        exec.shutdown();
	    }
	}

}