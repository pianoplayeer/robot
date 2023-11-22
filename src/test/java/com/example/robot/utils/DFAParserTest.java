package com.example.robot.utils;

import com.example.robot.data.DataPackage;
import com.example.robot.data.User;
import com.example.robot.data.repos.DataPackageRepository;
import com.example.robot.data.repos.UserRepository;
import com.example.robot.service.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class DFAParserTest {
	
	@Mock
	private RepositoryService mockReposService;
	
	@Mock
	private DataPackageRepository mockDataPackageRepos;
	
	@Mock
	private UserRepository mockUserRepos;
	
	@Autowired
	private ResourceLoader loader;

	@Autowired
	private Environment env;
	
	private DFAParser dfaParserUnderTest;
	
	private User user;
	
	@BeforeEach
	void setUp() {
		dfaParserUnderTest = new DFAParser(mockReposService, "username", loader, env);
	}
	
	@Test
	void testGetCurrentResponse() {
		// Setup
		// Run the test
		final String result = dfaParserUnderTest.getCurrentResponse();
		
		// Verify the results
		assertThat(result).isEqualTo("你好，请问有什么想要问的吗？（撸猫位置，撸狗位置）");
	}
	
	@Test
	void testTransferState() {
		// Setup
		// Run the test
		dfaParserUnderTest.transferState("撸猫位置");
		
		// Verify the results
		assertThat(dfaParserUnderTest.getCurrentResponse()).isEqualTo("撸猫位置在宿舍楼前");
		
		dfaParserUnderTest.transferState("撸狗位置");
		
		// Verify the results
		assertThat(dfaParserUnderTest.getCurrentResponse()).isEqualTo("撸狗位置在宿舍楼后");
		
		dfaParserUnderTest.transferState("fsijojfoisjdof");
		
		// Verify the results
		assertThat(dfaParserUnderTest.getCurrentResponse()).isEqualTo("你好，请问有什么想要问的吗？（撸猫位置，撸狗位置）");
	}
	
	@Test
	void testHasPackageAndAfford() {
		// Setup
		user = new User("username", "pswd");
		when(mockReposService.getDataPackageRepos()).thenReturn(mockDataPackageRepos);
		when(mockReposService.getUserRepos()).thenReturn(mockUserRepos);
		when(mockDataPackageRepos.findByPackageName("套餐1")).thenReturn(new DataPackage("套餐1", 100));
		when(mockDataPackageRepos.findByPackageName("套餐x")).thenThrow(new ActionException("套餐不存在"));
		when(mockUserRepos.findByUsername("username")).thenReturn(user);
		
		user.setBalance(200);
		dfaParserUnderTest.transferState("套餐1");
		// Run the test
		dfaParserUnderTest.hasPackageAndAfford();
		
		dfaParserUnderTest.transferState("套餐x");
		// Run the test
		assertThatThrownBy(() -> dfaParserUnderTest.hasPackageAndAfford()).isInstanceOf(ActionException.class);
		
		// Verify the results
	}
	
	@Test
	void testBuyPackage() {
		// Setup
		user = new User("username", "pswd");
		when(mockReposService.getDataPackageRepos()).thenReturn(mockDataPackageRepos);
		when(mockReposService.getUserRepos()).thenReturn(mockUserRepos);
		when(mockDataPackageRepos.findByPackageName("套餐1")).thenReturn(new DataPackage("套餐1", 100));
		when(mockUserRepos.findByUsername("username")).thenReturn(user);
		when(mockUserRepos.save(user)).thenReturn(user);
		
		dfaParserUnderTest.transferState("套餐1");
		// Run the test
		dfaParserUnderTest.buyPackage();
		
		// Verify the results
		verify(mockUserRepos).save(user);
	}
	
	@Test
	void testIsNumber() {
		// Setup
		// Run the test
		dfaParserUnderTest.transferState("100");
		dfaParserUnderTest.isNumber();
		
		dfaParserUnderTest.transferState("33a");
		assertThatThrownBy(() -> dfaParserUnderTest.isNumber()).isInstanceOf(ActionException.class);
	}
	
	@Test
	void testIsNumber_ThrowsActionException() {
		// Setup
		// Run the test
		assertThatThrownBy(() -> dfaParserUnderTest.isNumber()).isInstanceOf(ActionException.class);
	}
	
	@Test
	void testFindPackage() {
		// Setup
		when(mockReposService.getUserRepos()).thenReturn(mockUserRepos);
		when(mockUserRepos.findByUsername("username")).thenReturn(new User("username", "dfs"));
		
		assertThatThrownBy(() -> dfaParserUnderTest.findPackage()).isInstanceOf(ActionException.class);
	}
}
