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

/**
 * dfa脚本的解释器，即每个用户对应的客服机器人的底层实现
 *
 */
@Data
@Slf4j
public class DFAParser {
	
	/**
	 * 以HashMap形式存储dfa脚本中各个状态对应的信息
	 */
	private HashMap<String, Object> stateMap;
	
	/**
	 * 当前用户所处的状态，规定start为起始状态
	 */
	private String state = "start";
	
	/**
	 * 用户当前输入的内容，即机器人需要进行回应的消息
	 */
	private String currentMsg = "";
	
	/**
	 * 相应用户与机器人的聊天消息记录
	 */
	private ArrayList<Message> msgList = new ArrayList<>();
	
	private String username;
	private RepositoryService reposService;
	
	public DFAParser(RepositoryService reposService, String username,
					 ResourceLoader loader, Environment env) {
		this.username = username;
		this.reposService = reposService;
		
		// 从配置文件中读取dfa脚本名称，并从resources文件夹中进行读取
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
	
	/**
	 * 获取当前状态根据用户消息进行的回应
	 * @return 响应消息
	 */
	@SuppressWarnings("unchecked")
	public String getCurrentResponse() {
		StringBuilder builder = new StringBuilder();
		Map<String, Object> current = (Map<String, Object>) stateMap.get(state);
		ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) current.get("response");
		
		for (Map<String, String> map : list) {
			// 对action进行处理，反射调用对应的操作方法
			if (map.containsKey("action")) {
				try {
					Method method = this.getClass().getDeclaredMethod(map.get("action"));
					
					if (method.getReturnType() != void.class) {
						builder.append(method.invoke(this));
					} else {
						method.invoke(this);
					}
				} catch (Exception e) {
					// 如果是ActionException，则直接返回异常信息
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
	
	/**
	 * 根据用户消息进行状态转移，并进行currentMsg的更新
	 * @param msg 当前用户消息
	 */
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
	
	/**
	 * 判断用户是否已购买输入的套餐，已经购买则抛出异常，负责继续判断余额是否足够购买，不够则抛出相应异常
	 * @throws ActionException 用于包装异常信息并返回
	 */
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
	
	/**
	 * 购买套餐，即将套餐添加到用户的套餐列表中，并扣除相应的余额
	 */
	public void buyPackage() {
		DataPackage p = reposService.getDataPackageRepos().findByPackageName(currentMsg);
		User user = reposService.getUserRepos().findByUsername(username);
		
		user.getPackageList().add(p);
		user.setBalance(user.getBalance() - p.getPackagePrice());
		
		reposService.getUserRepos().save(user);
	}
	
	/**
	 * 查找用户余额，并格式化为字符串
	 * @return 格式化后的余额字符串
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public String findBalance() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		// 创建DecimalFormat对象并设置格式
		DecimalFormat format = new DecimalFormat("#,##0.00");
		
		return format.format(((User) UserRepository.class.getDeclaredMethod("findByUsername", String.class)
											.invoke(reposService.getUserRepos(), username)).getBalance());
	}
	
	/**
	 * 判断用户输入的是否为数字
	 * @throws ActionException 用于包装异常信息并返回
	 */
	public void isNumber() throws ActionException {
		try {
			Double.parseDouble(currentMsg);
		} catch (NumberFormatException e) {
			throw new ActionException("请输入数字");
		}
	}
	
	/**
	 * 更新用户余额
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void updateBalance()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		
		UserRepository.class.getDeclaredMethod("updateBalanceByUsername", String.class, double.class)
							.invoke(reposService.getUserRepos(), username, Double.parseDouble(currentMsg));
	}
	
	/**
	 * 查找可以购买的套餐（还未被购买的）
	 * @return 可购买套餐的字符串
	 * @throws ActionException 用于包装异常信息并返回
	 */
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
	
	/**
	 * 判断用户是否已经购买了输入的套餐
	 * @throws ActionException 用于包装异常信息并返回
	 */
	public void inBoughtPackages() throws ActionException {
		List<DataPackage> boughtPackages = reposService.getUserRepos().findByUsername(username).getPackageList();
		
		if (!boughtPackages.contains(reposService.getDataPackageRepos().findByPackageName(currentMsg))) {
			throw new ActionException("您未购买该套餐");
		}
	}
	
	/**
	 * 退订套餐，即将套餐从用户的套餐列表中移除，并退还相应的余额
	 */
	public void unsubscribePackage() {
		User user = reposService.getUserRepos().findByUsername(username);
		DataPackage p = reposService.getDataPackageRepos().findByPackageName(currentMsg);
		
		user.getPackageList().remove(p);
		user.setBalance(user.getBalance() + p.getPackagePrice());
		
		reposService.getUserRepos().save(user);
	}
	
	/**
	 * 查找已经购买的套餐
	 * @return 已购买套餐的字符串
	 * @throws ActionException 用于包装异常信息并返回
	 */
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
