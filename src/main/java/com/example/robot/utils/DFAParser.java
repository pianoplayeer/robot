package com.example.robot.utils;

import com.example.robot.data.DataPackage;
import com.example.robot.data.User;
import com.example.robot.data.repos.UserRepository;
import com.example.robot.service.RepositoryService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date 2023/11/15
 * @package com.example.robot.utils
 */

@Data
@Slf4j
public class DFAParser {
	
	private HashMap<String, Object> stateMap;
	private String state = "start";
	private String currentMsg = "";
	private ArrayList<Message> msgList = new ArrayList<>();
	
	private String username;
	private RepositoryService reposService;
	
	public DFAParser(RepositoryService reposService, String username,
					 ResourceLoader loader, Environment env) {
		this.username = username;
		this.reposService = reposService;
		
		String script = env.getProperty("dfa.filename");
		String filePath = "classpath:/static/script/" + script;
		Resource resource = loader.getResource(filePath);
		log.info("script: {}", script);
		
		try(
				InputStream inputStream = new FileInputStream(resource.getFile())
		) {
			Yaml yaml = new Yaml();
			stateMap = yaml.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public String getCurrentResponse() {
		StringBuilder builder = new StringBuilder();
		Map<String, Object> current = (Map<String, Object>) stateMap.get(state);
		ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) current.get("response");
		
		for (Map<String, String> map : list) {
			if (map.containsKey("action")) {
				try {
					Method method = this.getClass().getDeclaredMethod(map.get("action"));
					
					if (method.getReturnType() != void.class) {
						builder.append(method.invoke(this));
					} else {
						method.invoke(this);
					}
				} catch (Exception e) {
					if (e.getCause() instanceof ActionException) {
						builder.setLength(0);
						builder.append(e.getCause().getMessage());
						break;
					}
					e.printStackTrace();
				}
			} else if (map.containsKey("content")) {
				builder.append(map.get("content"));
			}
		}
		
		return builder.toString();
	}
	
	@SuppressWarnings("unchecked")
	public void transferState(String msg) {
		currentMsg = msg;
		Map<String, String> shift = (Map<String, String>) ((Map<String, Object>) stateMap.get(state)).get("shift");
		
		if (shift.containsKey(msg)) {
			state = shift.get(msg);
		} else {
			state = shift.get("default");
			
			if (state == null) {
				state = "start";
			}
		}
	}
	
	public void hasPackageAndAfford() throws ActionException {
		DataPackage p = reposService.getDataPackageRepos().findByPackageName(currentMsg);
		if (p == null) {
			throw new ActionException("您输入的套餐不存在");
		}
		
		User user = reposService.getUserRepos().findByUsername(username);
		if (user.getPackageList().contains(p)) {
			throw new ActionException("您已经购买了该套餐");
		} else if (user.getBalance() < p.getPackagePrice()) {
			throw new ActionException("您的余额不足");
		}
	}
	
	public void buyPackage() {
		DataPackage p = reposService.getDataPackageRepos().findByPackageName(currentMsg);
		User user = reposService.getUserRepos().findByUsername(username);
		
		user.getPackageList().add(p);
		user.setBalance(user.getBalance() - p.getPackagePrice());
		
		reposService.getUserRepos().save(user);
	}
	
	public String findBalance() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		// 创建DecimalFormat对象并设置格式
		DecimalFormat format = new DecimalFormat("#,##0.00");
		
		return format.format(((User) UserRepository.class.getDeclaredMethod("findByUsername", String.class)
											.invoke(reposService.getUserRepos(), username)).getBalance());
	}
	
	public void isNumber() throws ActionException {
		try {
			Double.parseDouble(currentMsg);
		} catch (NumberFormatException e) {
			throw new ActionException("请输入数字");
		}
	}
	
	public void updateBalance()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		
		UserRepository.class.getDeclaredMethod("updateBalanceByUsername", String.class, double.class)
							.invoke(reposService.getUserRepos(), username, Double.parseDouble(currentMsg));
	}
	
	// 查找可以购买的套餐（还未被购买的）
	public String findAvailablePackage() throws ActionException {
		List<DataPackage> boughtPackages = reposService.getUserRepos().findByUsername(username).getPackageList();
		StringBuilder builder = new StringBuilder();
		
		for (DataPackage p : reposService.getDataPackageRepos().findAll()) {
			if (!boughtPackages.contains(p)) {
				builder.append(p.getPackageName()).append("\n");
			}
		}
		
		if (builder.length() == 0) {
			throw new ActionException("无可购买套餐");
		}
		
		return builder.toString();
	}
	
	public void inBoughtPackages() throws ActionException {
		List<DataPackage> boughtPackages = reposService.getUserRepos().findByUsername(username).getPackageList();
		
		if (!boughtPackages.contains(reposService.getDataPackageRepos().findByPackageName(currentMsg))) {
			throw new ActionException("您未购买该套餐");
		}
	}
	
	public void unsubscribePackage() {
		User user = reposService.getUserRepos().findByUsername(username);
		DataPackage p = reposService.getDataPackageRepos().findByPackageName(currentMsg);
		
		user.getPackageList().remove(p);
		user.setBalance(user.getBalance() + p.getPackagePrice());
		
		reposService.getUserRepos().save(user);
	}
	
	// 查找已经购买的套餐
	public String findPackage() throws ActionException {
		StringBuilder builder = new StringBuilder();
		List<DataPackage> boughtPackages = reposService.getUserRepos()
													.findByUsername(username)
													.getPackageList();
		
		if (boughtPackages.isEmpty()) {
			throw new ActionException("您未购买任何套餐");
		}
		
		builder.append("您已购买的套餐有：\n");
		for (DataPackage p : boughtPackages) {
			
			builder.append(p.getPackageName()).append("\n");
		}
		
		return builder.toString();
	}
}

class ActionException extends RuntimeException {
	public ActionException(String message) {
		super(message);
	}
}
