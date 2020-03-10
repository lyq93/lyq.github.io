package com.sz.lyq.asyncServletDemo.servlet;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(asyncSupported = true, urlPatterns = "/async")
public class AsyncServlet extends HttpServlet {

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AsyncContext asyncContext = req.startAsync();

        resp.setContentType("text/html;charset=UTF-8");

        ServletContext servletContext = req.getServletContext();
        PrintWriter writer = resp.getWriter();
        writer.write(threadInfo("异步开始！"));

        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent asyncEvent) throws IOException {
                ServletResponse response = asyncContext.getResponse();
                PrintWriter printWriter = response.getWriter();
                printWriter.write(threadInfo("执行完毕!"));
            }

            @Override
            public void onTimeout(AsyncEvent asyncEvent) throws IOException {
                servletContext.log(threadInfo("超时！"));
            }

            @Override
            public void onError(AsyncEvent asyncEvent) throws IOException {
                servletContext.log(threadInfo("异常！"));
            }

            @Override
            public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
                servletContext.log(threadInfo("异步开始."));
            }
        });

        asyncContext.start(new Runnable() {
            @Override
            public void run() {
                ServletResponse response = asyncContext.getResponse();
                try {
                    PrintWriter resultWriter = response.getWriter();
                    resultWriter.write(threadInfo("异步线程开始！"));
                    asyncContext.complete();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    public String threadInfo(String eventString) {
        return "当前线程：" + Thread.currentThread().getName() + eventString;
    }
}
