package controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dao.ScheduleDao;
import lombok.extern.log4j.Log4j;
import vo.DateData;
import vo.ScheduleDto;

	
@Controller
@Log4j
public class HomeController {
	@Autowired
	public SqlSession sqlSession;
	
	ScheduleDao schedule_dao;
	public void setSchedule_dao(ScheduleDao schedule_dao) {
		this.schedule_dao = schedule_dao;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value = { "/", "/calendar.do" }, method = RequestMethod.GET)
	public String calendar(Model model, HttpServletRequest request, DateData dateData) {
		Calendar cal = Calendar.getInstance();
		DateData calendarData;
		// 검색 날짜
		if(dateData.getDate().equals("") && dateData.getMonth().equals("")) {
			dateData = new DateData(String.valueOf(cal.get(Calendar.YEAR)),
					String.valueOf(cal.get(Calendar.MONTH)),
					String.valueOf(cal.get(Calendar.DATE)), null, null);
		}
		
		Map<String, Integer> today_info = dateData.today_info(dateData);
		List<DateData> dateList = new ArrayList<DateData>();
		
		//검색 날짜 end
		//ScheduleDao scheduleDao = sqlSession.getMapper(ScheduleDao.class);
		List<ScheduleDto> Schedule_list = schedule_dao.schedule_list(dateData);
		
		// 달력 데이터를 넣기 위한 배열 추가
		ScheduleDto[][] schedule_data_arr = new ScheduleDto[32][4];
		if(Schedule_list.isEmpty() != true) {
			int j=0;
			for(int i=0; i<Schedule_list.size(); i++) {
				int date = Integer.parseInt(String.valueOf(Schedule_list.get(i).getSchedule_date()).substring(
						String.valueOf(Schedule_list.get(i).getSchedule_date()).length()-2,
						String.valueOf(Schedule_list.get(i).getSchedule_date()).length()));
				if (i>0) {
					int date_before = Integer.parseInt(String.valueOf(Schedule_list.get(i-1).getSchedule_date())
							.substring(String.valueOf(Schedule_list.get(i-1).getSchedule_date()).length()-2,
									String.valueOf(Schedule_list.get(i-1).getSchedule_date()).length()));
					if(date_before == date) {
						j = j + 1;
						schedule_data_arr[date][j] = Schedule_list.get(i);
					} else {
						j = 0;
						schedule_data_arr[date][j] = Schedule_list.get(i);
					}
				} else {
					schedule_data_arr[date][j] = Schedule_list.get(i);
				}
			}
		}
		
		// 실질적인 달력 데이터 리스트에 데이터 삽입 시작.
		// 일단 시작 인덱스까지 아무것도 없는 데이터 삽입
		for(int i=1; i<today_info.get("start"); i++) {
			calendarData = new DateData(null,null,null,null,null);
			dateList.add(calendarData);
		}
		
		// 날짜 삽입
		for(int i=today_info.get("startDay"); i<=today_info.get("endDay"); i++) {
			ScheduleDto[] schedule_data_arr3 = new ScheduleDto[4];
			schedule_data_arr3 = schedule_data_arr[i];
			
			if(i==today_info.get("today")) {
				calendarData = new DateData(String.valueOf(dateData.getYear()),
						String.valueOf(dateData.getMonth()),
						String.valueOf(i), "today", schedule_data_arr3);
			} else {
				calendarData = new DateData(String.valueOf(dateData.getYear()),
						String.valueOf(dateData.getMonth()),
						String.valueOf(i), "normal_date", schedule_data_arr3);
			}
			dateList.add(calendarData);
		}
		
		// 달력 빈 곳 빈 데이터로 삽입
		int index = 7 - dateList.size() % 7;
		if(dateList.size() % 7 != 0) {
			for(int i=0; i<index; i++) {
				calendarData = new DateData(null,null,null,null,null);
				dateList.add(calendarData);
			}
		}
		
		// 배열에 담음
		model.addAttribute("dateList", dateList);	// 날짜 데이터 배열
		model.addAttribute("today_info", today_info);
		/* return "schedule/calendar"; */
		return "/WEB-INF/views/schedule/calendar.jsp";
	}
	
//	@RequestMapping(value = "schedule_add.do", method = RequestMethod.GET)
//	public String schedule_add(HttpServletRequest request, ScheduleDto scheduleDto, RedirectAttributes rttr) {
////		ScheduleDao scheduleDao = sqlSession.getMapper(ScheduleDao.class);
////		int count = scheduleDao.before_schedule_add_search(scheduleDto);
//		int count = schedule_dao.before_schedule_add_search(scheduleDto);
//		String message = "";
////		String url = "redirect:calendar.do";
//		if (count >= 4) {
//			message = "스케쥴은 최대 4개만 등록 가능합니다.";
//		} else {
//			schedule_dao.schedule_add(scheduleDto);
//			message = "스케쥴이 등록되었습니다";
//		}
//		rttr.addFlashAttribute("message", message);
////		return url;
//		return "redirect:calendar.do";
//	}
	
	@RequestMapping("/schedule_add.do")
	public String schedule_add(ScheduleDto scheduleDto, RedirectAttributes rttr) {
		int count = schedule_dao.before_schedule_add_search(scheduleDto);
		String message = "";
		if (count >= 4) {
			message = "하루 스케쥴은 최대 4개만 등록 가능합니다.";
		} else {
			schedule_dao.schedule_add(scheduleDto);
			message = "스케쥴이 등록되었습니다";
		}
		rttr.addFlashAttribute("message", message);
//		int res = schedule_dao.schedule_add(scheduleDto);
		System.out.println("HWang");
		return "redirect:calendar.do";
	}	
	
	/* 일정정보 출력 */
	@RequestMapping(value="/schedule_show", method=RequestMethod.GET)
	public String schedule_show (Model model, HttpServletRequest request, @RequestParam("schedule_idx") int idx, RedirectAttributes rttr) {
		//ScheduleDao scheduleDao = sqlSession.getMapper(ScheduleDao.class);
//		String url = "redirect:calendar.do";
		schedule_dao.get(idx);
		model.addAttribute("schedule_show",schedule_dao.get(idx));
		return null;
	}
	
	@RequestMapping("/schedule_show.do")
	public String article_info_list(Model model, int schedule_idx) {
		//List<ScheduleDto> list= schedule_dao.get(schedule_idx);
		ScheduleDto list = schedule_dao.get(schedule_idx);	// dao에서 selectOne을 사용하면, forEach를 사용하지 않고 바로 schedule_show.schedule_idx처럼 사용 가능하다
		model.addAttribute("schedule_show",list);
		System.out.println("schedule_show " + list);
		return "/WEB-INF/views/schedule_show.jsp";
	}
	
	// 모든 일정 표기
	@RequestMapping("/calendar_list.do")
	public String history_list(Model model) {
		List<ScheduleDto> list= schedule_dao.calendar_list();
		model.addAttribute("dateList",list);
		System.out.println(list);
		return "/WEB-INF/views/schedule/calendar_list.jsp";
	}
	
	/*
	 * //글 수정 폼으로 이동
	 * 
	 * @RequestMapping("/schedule_show.do") public String modify_form( Model model,
	 * int idx) { //idx는 중복값이 없으니까 idx로 구분하겠다. idx를 모델에 담는다.
	 * 
	 * ScheduleDto vo = schedule_dao.get(idx);
	 * 
	 * if(vo != null) { model.addAttribute("vo", vo); //vo에 모든정보 담긴채로 모디파이로감 }
	 * return "WEB-INF/views/schedule_show.jsp"; }
	 */
	
//	@RequestMapping("modify")
//	public String schedule_modify (Model model, HttpServletRequest request, ScheduleDto scheduleDto, RedirectAttributes rttr) {
//		//ScheduleDao scheduleDao = sqlSession.getMapper(ScheduleDao.class);
//		schedule_dao.update(scheduleDto);
//		model.addAttribute("schedule_modify",schedule_dao.update(scheduleDto));
//		return "/modify";
//	}
	
//	@RequestMapping("/modify.do")
//	public String modify(ScheduleDto scheduleDto, HttpServletRequest request) {
//		System.out.println("들어오니? 수정 " + scheduleDto);
//		int res = schedule_dao.update(scheduleDto);
//		
//		return "/WEB-INF/views/schedule_show.jsp";
//	}
	
	@RequestMapping("/modify.do")
	@ResponseBody
	public String modify(int schedule_idx, ScheduleDto scheduleDto) {
		System.out.println("들어오니? idx " + schedule_idx);
		System.out.println("들어오니? DTO " + scheduleDto);
		int res = schedule_dao.update(scheduleDto);

		String result = "no";
		if(res != 0) {
			result="yes";
		}
		return result;
	}
	
//	@RequestMapping("/modify.do")
//	@RequestMapping(value = "/modify.do", method = { RequestMethod.POST })
//	public String modify(@RequestBody("idx") String idx) throws Exception {
//		System.out.println("들어오니? idx " + schedule_idx);
//		System.out.println("들어오니? DTO " + scheduleDto);
//		int res = schedule_dao.update(scheduleDto);
//
//		String result = "no";
//		if(res != 0) {
//			result="yes";
//		}
//		return result;
//	}
	
//	@RequestMapping(value="delete.do", method=RequestMethod.GET)
//	public String schedule_delete (Model model, HttpServletRequest request, 
//			ScheduleDto scheduleDto, RedirectAttributes rttr) {
//		//ScheduleDao scheduleDao = sqlSession.getMapper(ScheduleDao.class);
//		schedule_dao.delete(scheduleDto);
//		model.addAttribute("schedule_delete",schedule_dao.delete(scheduleDto));
//		return null;
//	}
	
	@RequestMapping("/delete.do")
	@ResponseBody
	public String schedule_delete (int schedule_idx) {
		//ScheduleDao scheduleDao = sqlSession.getMapper(ScheduleDao.class);
		System.out.println("외 않두러화 schedule_idx : " + schedule_idx);
		int res = schedule_dao.delete(schedule_idx);
		
		String result = "no";
		if(res != 0) {
			result="yes";
		}
		return result;
	}
	
}











































