package com.github.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChromeDriverHandler {

	private void handle() {
		while (true) {

			String url = "http://127.0.0.1:9515/wd/hub/sessions";
			String response = sendGetRequest(url);

			try {
				if (!response.isEmpty() || response != null) {
					JSONObject responseJson = new JSONObject(response);
					JSONArray valueJsonArray = responseJson.getJSONArray("value");

					if (valueJsonArray.length() < 1) {
						System.out.println("******* Handling Chromedriver, Making Responsive. ********** ");

						if (System.getProperty("os.name").trim().toLowerCase().matches("^windows.*")) {
							Runtime.getRuntime()
									.exec(new String[] { "cmd.exe", "/C", "taskkill /f /im chromedriver.exe" });
						} else {
							Runtime.getRuntime().exec("killall chromedriver");
						}
					}
				}
			} catch (JSONException | IOException e1) {
				e1.printStackTrace();
			}

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
		}
	}

	public static Thread getHandler() {
		Thread handlerTread = new Thread(new Runnable() {
			@Override
			public void run() {
				new ChromeDriverHandler().handle();
			}
		});

		return handlerTread;
	}

	private String sendGetRequest(String ServerURL) {
		StringBuffer result = new StringBuffer();
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;

		try {
			httpclient = HttpClients.createDefault();

			ServerURL = ServerURL.replace("%%", "").trim();
			HttpGet GetRequest = new HttpGet(ServerURL);
			try {
				response = httpclient.execute(GetRequest);
			} catch (HttpHostConnectException h) {
				System.out.println("Couldn't connect to host: " + ServerURL);
			}

			BufferedReader rd = null;
			try {
				rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			} catch (NullPointerException e) {
				System.out.println("No Response Received. ");
			}

			if (rd != null) {
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
			}
		} catch (Exception e) {
		} finally {

			try {
				response.close();
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return result.toString();
	}

}
