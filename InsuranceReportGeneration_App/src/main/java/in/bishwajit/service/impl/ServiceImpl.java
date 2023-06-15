package in.bishwajit.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import in.bishwajit.entity.CitizenPlan;
import in.bishwajit.repo.CitizenPlanRepository;
import in.bishwajit.request.SearchRequest;
import in.bishwajit.service.ReportService;
import in.bishwajit.util.EmailUtils;
import in.bishwajit.util.ExcelGenerator;
import in.bishwajit.util.PdfGenerator;

@Service
public class ServiceImpl implements ReportService {

	@Autowired
	private CitizenPlanRepository planRepo;

	@Autowired
	private ExcelGenerator excelGenerator;

	@Autowired
	private PdfGenerator pdfGenerator;

	@Autowired
	private EmailUtils emailUtils;

	@Override
	public List<String> getPlanNames() {
		return planRepo.getPlanNames();
	}

	@Override
	public List<String> getPlanStatuses() {
		return planRepo.getPlanStatus();
	}

	@Override
	public List<CitizenPlan> search(SearchRequest request) {

		CitizenPlan entity = new CitizenPlan();

		if (null != request.getPlanName() && !"".equals(request.getPlanName())) {
			entity.setPlanName(request.getPlanName());
		}
		if (null != request.getPlanStatus() && !"".equals(request.getPlanStatus())) {
			entity.setPlanStatus(request.getPlanStatus());
		}
		if (null != request.getGender() && !"".equals(request.getGender())) {
			entity.setGender(request.getGender());
		}

		if (null != request.getStartDate() && !"".equals(request.getStartDate())) {
			String startDate = request.getStartDate();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			// converting string to localdate
			LocalDate localDate = LocalDate.parse(startDate, formatter);
			entity.setPlanStartDate(localDate);
		}

		if (null != request.getEndDate() && !"".equals(request.getEndDate())) {
			String endDate = request.getEndDate();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			// converting string to localdate
			LocalDate localDate = LocalDate.parse(endDate, formatter);
			entity.setPlanEndDate(localDate);
		}

		return planRepo.findAll(Example.of(entity));
	}

	@Override
	public boolean exportExcel(HttpServletResponse response) throws Exception {

		File f = new File("Plans.xls");

		List<CitizenPlan> plans = planRepo.findAll();
		excelGenerator.generate(response, plans, f);

		String subject = "Test mail subject";
		String body = "<h1>Test mail body</h1>";
		String to = "pravakarmohapatra8@gmail.com";

		emailUtils.sendEmail(subject, body, to, f);

		f.delete();

		return true;
	}

	@Override
	public boolean exportPdf(HttpServletResponse response) throws Exception {

		File f = new File("Plans.pdf");

		List<CitizenPlan> plans = planRepo.findAll();

		pdfGenerator.generate(response, plans, f);

		String subject = "Test mail subject";
		String body = "<h1>Test mail body</h1>";
		String to = "pravakarmohapatra8@gmail.com";

		emailUtils.sendEmail(subject, body, to, f);

		f.delete();

		return true;
	}

}
