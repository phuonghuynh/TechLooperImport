package com.techlooper.service;

import com.techlooper.pojo.AccountModel;
import com.techlooper.pojo.GravatarModel;
import com.techlooper.pojo.PhotoModel;
import com.techlooper.pojo.UrlModel;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by chrisshayan on 2/21/15.
 */
public class GravatarServiceTest {
    public static final String EMAIL = "hamedshayan@gmail.com";

    @Test
    public void getMD5Hash() {
        final String myEmailHash = "41d0bb0711c4102dc381d5e731973d2c"; // Generated by http://www.md5.cz/
        GravatarService gravatarService = new GravatarService();
        assertEquals(myEmailHash, gravatarService.getMD5Hash(EMAIL));
    }

    @Test
    public void testFindGravatarProfile() throws Exception {
        GravatarService gravatarService = new GravatarService();
        final GravatarModel gravatarProfile = gravatarService.findGravatarProfile(EMAIL);
        assertNotNull(gravatarProfile);

        final String myProfileUrl = "http://gravatar.com/shayanchris";
        final String myThumbnailUrl = "https://secure.gravatar.com/avatar/41d0bb0711c4102dc381d5e731973d2c";
        final String preferredUsername = "shayanchris";
        final String displayName = "shayanchris";
        final String aboutMe = "white-russian";

        assertEquals(myProfileUrl, gravatarProfile.getProfileUrl());
        assertEquals(myThumbnailUrl, gravatarProfile.getThumbnailUrl());
        assertEquals(preferredUsername, gravatarProfile.getPreferredUsername());
        assertEquals(displayName, gravatarProfile.getDisplayName());
        assertTrue(gravatarProfile.getAboutMe().contains(aboutMe));
        assertNotNull(gravatarProfile.getPhotos());
        final PhotoModel photoModel = gravatarProfile.getPhotos().get(0);
        assertNotNull(photoModel);
        assertEquals("thumbnail", photoModel.getType());
        assertEquals("https://secure.gravatar.com/avatar/41d0bb0711c4102dc381d5e731973d2c", photoModel.getValue());
    }

    @Test
    public void testFindGravatarProfile205e460b479e2e5b48aec07710c08d50() throws Exception{
        GravatarService gravatarService = new GravatarService();
        // @see more at https://en.gravatar.com/205e460b479e2e5b48aec07710c08d50.json
        final GravatarModel gravatarProfile = gravatarService.findGravatarProfile("beau@dentedreality.com.au");
        assertNotNull(gravatarProfile);
        assertNotNull(gravatarProfile.getProfileBackground());
        assertEquals("#9c9c9c", gravatarProfile.getProfileBackground().getColor());
        assertEquals("https://secure.gravatar.com/bg/1428/e9db3f026b7ce7748e58169cecb4980f", gravatarProfile.getProfileBackground().getUrl());

        assertNotNull(gravatarProfile.getName());
        assertEquals("Beau", gravatarProfile.getName().getGivenName());
        assertEquals("Lebens", gravatarProfile.getName().getFamilyName());
        assertEquals("Beau Lebens", gravatarProfile.getName().getFormatted());

        assertNotNull(gravatarProfile.getPhoneNumbers());
        assertNotNull(gravatarProfile.getPhoneNumbers().get(0));
        assertEquals("mobile", gravatarProfile.getPhoneNumbers().get(0).getType());
        assertEquals("+1-415-279-0783", gravatarProfile.getPhoneNumbers().get(0).getValue());

        assertNotNull(gravatarProfile.getEmails());
        assertNotNull(gravatarProfile.getEmails().get(0));
        assertTrue(gravatarProfile.getEmails().get(0).isPrimary());
        assertEquals("beau@dentedreality.com.au", gravatarProfile.getEmails().get(0).getValue());

        assertNotNull(gravatarProfile.getIms());
        assertNotNull(gravatarProfile.getIms().get(0));
        assertTrue(gravatarProfile.getIms().size() == 5);

        final List<AccountModel> accounts = gravatarProfile.getAccounts();
        assertNotNull(accounts);
        assertNotNull(accounts.get(0));
        assertTrue(accounts.size() == 8);
        accounts.stream().filter(account -> account.getDomain().equals("linkedin.com")).forEach(account -> {
            assertEquals("beaulebens", account.getDisplay());
            assertEquals("beaulebens", account.getUsername());
            assertEquals("linkedin", account.getShortName());
            assertTrue(account.isVerified());
            assertEquals("http://www.linkedin.com/in/beaulebens", account.getUrl());
        });

        final List<UrlModel> urls = gravatarProfile.getUrls();
        assertNotNull(urls);
        assertNotNull(urls.get(0));
        assertTrue(urls.size() == 6);
        urls.stream().filter(url -> url.getTitle().equals("Gravatar")).forEach(url -> {
            assertEquals("http://gravatar.com", url.getValue());
        });
    }
}
