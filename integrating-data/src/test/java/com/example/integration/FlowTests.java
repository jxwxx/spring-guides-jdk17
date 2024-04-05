package com.example.integration;

import com.rometools.rome.feed.synd.SyndEntryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.endpoint.SourcePollingChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest({ "auto.startup=false", "feed.file.name=Test" })
public class FlowTests {
    @Autowired
    private SourcePollingChannelAdapter newsAdapter;

    @Autowired
    private MessageChannel news;

    public void test() throws Exception {
        assertThat(this.newsAdapter.isRunning()).isFalse();
        SyndEntryImpl syndEntry = new SyndEntryImpl();
        syndEntry.setTitle("Test Title");
        syndEntry.setLink("http://characters/frodo");
        File out = new File("/tmp/si/Test");
        out.delete();
        assertThat(out.exists()).isFalse();
        this.news.send(MessageBuilder.withPayload(syndEntry).build());
        assertThat(out.exists()).isTrue();
        BufferedReader br = new BufferedReader(new FileReader(out));
        String line = br.readLine();
        assertThat(line).isEqualTo("Test Title @ http://characters/frodo");
        br.close();
        out.delete();
    }
}
