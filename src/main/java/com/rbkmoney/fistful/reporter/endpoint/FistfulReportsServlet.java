package com.rbkmoney.fistful.reporter.endpoint;

import dev.vality.fistful.reporter.ReportingSrv;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/fistful/reports")
@RequiredArgsConstructor
public class FistfulReportsServlet extends GenericServlet {

    private Servlet thriftServlet;

    private final ReportingSrv.Iface requestHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .build(ReportingSrv.Iface.class, requestHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }
}
