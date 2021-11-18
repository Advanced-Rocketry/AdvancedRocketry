package zmaster587.advancedRocketry.command;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;

public class ReflectionArgument implements ArgumentType<String> {
	
	Class<?> target;
	
	ReflectionArgument(Class<?> target)
	{
		this.target = target;
	}
	
	private static final DynamicCommandExceptionType commandException = new DynamicCommandExceptionType((p_212594_0_) -> new TranslationTextComponent("argument.star.invalid", p_212594_0_));

	@Override
	public String parse(StringReader p_parse_1_) throws CommandSyntaxException {
		return p_parse_1_.readString();
	}
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		
		List<String> list = new LinkedList<>();
		for(Field field : target.getFields()) {
			list.add(field.getName());
		}
		
		return context.getSource() instanceof ISuggestionProvider ? ISuggestionProvider.suggest(list, builder) : Suggestions.empty();
	}

	@Override
	public Collection<String> getExamples() {
		return new LinkedList<>();
	}

	public static ReflectionArgument getReflected(Class<?> clazz)
	{
		return new ReflectionArgument(clazz);
	}
	
	public static String getReflectionArgument(CommandContext<CommandSource> source, String name, Class<?> target) throws CommandSyntaxException
	{
		String arg = source.getArgument(name, String.class);
		
		for(Field field : target.getFields()) {
			if(field.getName() == arg)
				return arg;
		}
		
		throw commandException.create(arg);
	}
}
