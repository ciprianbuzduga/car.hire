package ro.agilehub.javacourse.car.hire.base.test;

import org.junit.runner.Runner;
import org.junit.runners.parameterized.TestWithParameters;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.parameterized.BlockJUnit4ClassRunnerWithParameters;
import org.junit.runners.parameterized.ParametersRunnerFactory;

public class SpringJUnit4ClassRunnerFactory implements ParametersRunnerFactory {

	@Override
	public Runner createRunnerForTestWithParameters(TestWithParameters test)
			throws InitializationError {
		BlockJUnit4ClassRunnerWithParameters runnerWithParameters
						= new BlockJUnit4ClassRunnerWithParameters(test);
		return new SpringJUnit4ClassRunner(test.getTestClass().getJavaClass()) {
			@Override
			protected Object createTest() throws Exception {
				final Object testInstance = runnerWithParameters.createTest();
				getTestContextManager().prepareTestInstance(testInstance);
				return testInstance;
			}
		};
	}
}
