/*
 * Copyright (C) 2024 Emmanuel Godwin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.ceze.notification;

import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

public class Message
{

	private final Set<Email> recipients = new HashSet<>();
	private UUID id;
	private Email from;
	private String content;

	private Message()
	{
	}

	public Set<String> getRecipients()
	{
		return recipients.stream().map(Email::address).collect(Collectors.toSet());
	}

	public UUID getId()
	{
		return id;
	}

	public String getContent()
	{
		return content;
	}

	private void setContent(String content)
	{
		this.content = content;
	}

	public String getSender()
	{
		return from.address();
	}

	private record Email(@jakarta.validation.constraints.Email String address)
	{
	}

	public static class MessageBuilder
	{
		private final Message message = new Message();

		public static MessageBuilder withSender(String email)
		{
			MessageBuilder builder = new MessageBuilder();
			builder.message.from = new Email(email);
			return builder;
		}

		public MessageBuilder content(@NonNull String content)
		{
			message.setContent(content);
			return this;
		}

		public MessageBuilder recipient(String recipient)
		{
			this.message.recipients.add(new Email(recipient));
			return this;
		}

		public MessageBuilder recipients(Collection<String> recipients)
		{
			List<Email> emails = recipients.stream().map(Email::new).toList();
			this.message.recipients.addAll(emails);
			return this;
		}

		public Message build() throws IllegalArgumentException
		{
			Assert.notEmpty(message.recipients, "Message should contain at least one recipient");
			Assert.hasText(message.content, "Message should have some content");
			message.id = UUID.nameUUIDFromBytes(message.content.getBytes());
			return this.message;
		}
	}
}
