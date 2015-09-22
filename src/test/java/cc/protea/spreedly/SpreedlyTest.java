package cc.protea.spreedly;

import java.util.List;

import cc.protea.spreedly.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class SpreedlyTest {

	Spreedly spreedly;
	SpreedlyGatewayAccount testAccount;

	private SpreedlyGatewayAccount createTestGatewayAccount() {
		SpreedlyGatewayAccount account = new SpreedlyGatewayAccount();
		account.gatewayType = "test";
		SpreedlyGatewayCredential c = new SpreedlyGatewayCredential();
		c.name = "merchant_id_number";
		c.value = "12345";
		account.credentials.add(c);
		return spreedly.create(account);
	}

	private SpreedlyCreditCard testSpreedlyCreditCard() {
		SpreedlyCreditCard card = new SpreedlyCreditCard();
		card.setCardType(SpreedlyCardType.VISA);
		card.setNumber("4111111111111111");
		card.setEmail("testEmail@test.com");
		card.setData("testCardData");
		card.setMonth(11);
		card.setYear(2018);
		card.setFirstName("Joe");
		card.setLastName("Johnson");
		card.setVerificationValue("123");
		return card;
	}

	private SpreedlyPaymentMethod createTestPaymentMethod() {
		return spreedly.create(testSpreedlyCreditCard()).getPaymentMethod();
	}

	@Before
	public void before() {
		spreedly = SpreedlyTest.getSpreedly();
	}

    @Test
    public void testGetGatewayProviders() throws Exception {
    	if (spreedly == null) {
    		return;
    	}
    	final List<SpreedlyGatewayProvider> list = spreedly.listGatewayProviders();
    	Assert.assertNotNull(list);
    	Assert.assertTrue(! list.isEmpty());
    }

	@Test
	public void testCreateGatewayAccount() throws Exception {
		if (spreedly == null) {
			return;
		}
		SpreedlyGatewayAccount account = new SpreedlyGatewayAccount();
		account.gatewayType = "sage";
		SpreedlyGatewayCredential c = new SpreedlyGatewayCredential();
		c.name = "merchant_id_number";
		c.value = "12345";
		account.credentials.add(c);

		final SpreedlyGatewayAccount createdAccount = spreedly.create(account);
		Assert.assertNotNull(createdAccount);
		Assert.assertTrue(createdAccount.getToken() == "sage");
		Assert.assertTrue(createdAccount.getGatewayType() == "sage");
		Assert.assertTrue(!createdAccount.getCredentials().isEmpty());
		Assert.assertTrue(createdAccount.getCredentials().get(0).getName() == "merchant_id_number");
		Assert.assertTrue(createdAccount.getCredentials().get(0).getValue() == "12345");

		testAccount = createdAccount;
	}

	@Test
	public void testRedactGatewayAccount() throws Exception {
		if (spreedly == null) {
			return;
		}

		SpreedlyGatewayAccount redactedAccount = spreedly.redact(testAccount);
		Assert.assertTrue(redactedAccount.getState() == SpreedlyGatewayAccountState.REDACTED);
	}

	@Test
	public void testRetainGatewayAccount() throws Exception {
		if (spreedly == null) {
			return;
		}
		SpreedlyGatewayAccount retainedAccount = spreedly.retain(testAccount);
		Assert.assertTrue(retainedAccount.getState() == SpreedlyGatewayAccountState.RETAINED);
	}

	@Test
	public void testGetGatewayAccounts() throws Exception {
		if (spreedly == null) {
			return;
		}
		final List<SpreedlyGatewayAccount> list = spreedly.listGatewayAccounts();
		Assert.assertNotNull(list);
		Assert.assertTrue(! list.isEmpty());
	}

	@Test
	public void testCreatePaymentMethod() throws Exception {
		if (spreedly == null) {
			return;
		}

		SpreedlyTransactionResponse createPaymentMethodResponse = spreedly.create(testSpreedlyCreditCard());
		Assert.assertTrue(createPaymentMethodResponse.isSucceeded());
		Assert.assertTrue(createPaymentMethodResponse.getPaymentMethod().getPaymentMethodType() == SpreedlyPaymentMethodType.CREDIT_CARD);
		Assert.assertTrue(createPaymentMethodResponse.getPaymentMethod().getCardType() == SpreedlyCardType.VISA);
		Assert.assertTrue(createPaymentMethodResponse.getPaymentMethod().getStorageState() == SpreedlyStorageState.CACHED);
	}

	@Test
	public void testGetPaymentMethods() throws Exception {
		if (spreedly == null) {
			return;
		}

		SpreedlyPaymentMethod created = createTestPaymentMethod();
		SpreedlyPaymentMethod foundPaymentMethod = spreedly.getPaymentMethod(created.getToken());
		Assert.assertTrue(foundPaymentMethod.getPaymentMethodType() == SpreedlyPaymentMethodType.CREDIT_CARD);
		Assert.assertTrue(foundPaymentMethod.getCardType() == SpreedlyCardType.VISA);
		Assert.assertTrue(foundPaymentMethod.getStorageState() == SpreedlyStorageState.CACHED);
	}

    public static Spreedly getSpreedly() {
		final String environmentKey = SpreedlyTest.getEnvironmentKey();
		final String apiSecret = SpreedlyTest.getApiSecret();
		if (environmentKey == null || apiSecret == null) {
			return null;
		}
		return new Spreedly(environmentKey, apiSecret);
    }

	private static String getEnvironmentKey() {
		return System.getenv("SPREEDLYCORE_ENVIRONMENT_KEY");
	}

	private static String getApiSecret() {
		return System.getenv("SPREEDLYCORE_API_SECRET");
	}

}
