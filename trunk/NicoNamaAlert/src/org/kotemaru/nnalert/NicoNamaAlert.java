package org.kotemaru.nnalert;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;
import org.kotemaru.nnalert.Config.UserInfo;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import nanoxml.XMLElement;

public class NicoNamaAlert {
	private static final String UTF8 = "UTF8";
	private final String GETALERTINFO_URL = "http://live.nicovideo.jp/api/getalertinfo";
	private final String GETSTREAMINFO_URL = "http://live.nicovideo.jp/api/getstreaminfo/lv";
	private static final int RETRY_COUNT = 5;
	private static final int SERVER_PORT = 65000;

	private static class CommentServerInfo {
		String addr;
		String port;
		String thread;
		Socket sock;
	}

	public static void main(String[] args) throws IOException {
		Config.init(args[0]);
		RegistrationServer.start(SERVER_PORT);

		NicoNamaAlert nna = new NicoNamaAlert();
		while (true) {
			try {
				nna.start();
			} catch (Throwable t) {
				Log.e("Recover restart.", t);
			}
		}
	}

	private void start() throws Exception {
		CommentServerInfo info = getCommentServer();
		openCommentServer(info);
		doRun(info);
	}


	private CommentServerInfo getCommentServer() throws IOException {
		Xml xml = doPost(GETALERTINFO_URL, null);
		if (xml == null) return null;

		CommentServerInfo info = new CommentServerInfo();
		info.addr = xml.getContent("ms/addr");
		info.port = xml.getContent("ms/port");
		info.thread = xml.getContent("ms/thread");
		Log.d("CommentServer=" + info.addr + ":" + info.port);
		return info;
	}

	private CommentServerInfo openCommentServer(CommentServerInfo info) throws NumberFormatException, UnknownHostException,
			IOException {
		info.sock = new Socket(info.addr, Integer.parseInt(info.port));
		OutputStream out = info.sock.getOutputStream();
		// 最後に 0x00 が必要。仕様には書いてない。
		String data = "<thread thread=\"" + info.thread + "\" version=\"20061206\" res_from=\"-1\"/>\0";
		Log.d("thread=" + data);
		out.write(data.getBytes(UTF8));
		out.flush();
		return info;
	}

	private void doRun(CommentServerInfo info) throws IOException {
		InputStream in = info.sock.getInputStream();
		Log.d("doRun start");
		byte[] buff = new byte[1500];
		int n;
		while ((n = in.read(buff)) >= 0) { // TODO:手抜きread
			String chat = new String(buff, 0, n, UTF8);
			//Log.d(chat);
			String content = chat.replaceFirst("^<chat[^>]*>", "").replaceFirst("</chat>\0$", "");
			if (content.charAt(0) == '<') continue;
			String[] datas = content.split(",");
			if (datas.length == 3) {
				 sendToMatchUsers(datas[0], datas[1]);
			}
		}
	}

	private void sendToMatchUsers(String liveId, String commId) throws IOException {
		Map<String, UserInfo> all = Config.getUsers();
		for (Map.Entry<String, UserInfo> ent : all.entrySet()) {
			UserInfo uinfo = ent.getValue();
			if (uinfo.communities.contains(commId)) {
				sendToAndroid(uinfo,liveId, commId);
			}
		}
	}

	private Result sendToAndroid(UserInfo uinfo,String liveId, String commId) throws IOException {
		Xml xml = doPost(GETSTREAMINFO_URL + liveId, null);
		if (xml == null) return null;

		Log.i("Send to " + uinfo.mail+"="+ liveId);
		Sender sender = new Sender(Config.getApiKey());
		Message message = new Message.Builder()
				.addData("messageType", "onLive")
				.addData("liveId", liveId)
				.addData("community", xml.getContent("communityinfo/name"))
				.addData("title", xml.getContent("streaminfo/title"))
				.build();
		Result result = sender.send(message, uinfo.regId, RETRY_COUNT);
		return result;
	}

	private Xml doPost(String urlstr, String data) throws IOException {
		URL url = new URL(urlstr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		try {
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			OutputStream out = conn.getOutputStream();
			if (data != null) {
				out.write(data.getBytes(UTF8));
			}
			out.flush();

			int status = conn.getResponseCode();
			if (status != 200) {
				Log.e("doPost status=" + status);
			}

			XMLElement xml = new XMLElement();
			InputStreamReader reader = new InputStreamReader(conn.getInputStream(), UTF8);
			xml.parseFromReader(reader);
			return new Xml(xml);
		} catch (Throwable t) {
			Log.e(t.getMessage(), t);
		}
		return null;
	}

}
