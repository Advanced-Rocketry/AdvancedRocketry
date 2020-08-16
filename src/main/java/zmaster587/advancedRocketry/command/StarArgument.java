package zmaster587.advancedRocketry.command;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.dimension.DimensionManager;

public class StarArgument implements ArgumentType<String> {
	
	private static final DynamicCommandExceptionType commandException = new DynamicCommandExceptionType((p_212594_0_) -> {
		return new TranslationTextComponent("argument.star.invalid", p_212594_0_);
	});

	@Override
	public String parse(StringReader p_parse_1_) throws CommandSyntaxException {
		return p_parse_1_.readString();
	}
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		
		List<String> list = new LinkedList<String>();
		for(StellarBody star : DimensionManager.getInstance().getStars())
		{
			list.add(star.getName());
		}
		
		return context.getSource() instanceof ISuggestionProvider ? ISuggestionProvider.suggest(list, builder) : Suggestions.empty();
	}

	@Override
	public Collection<String> getExamples() {
		return new LinkedList<String>();
	}

	public static StarArgument getStar()
	{
		return new StarArgument();
	}
	
	public static StellarBody getStarArgument(CommandContext<CommandSource> source, String name) throws CommandSyntaxException
	{
		String arg = source.getArgument(name, String.class);
		for(StellarBody star : DimensionManager.getInstance().getStars())
		{
			if(star.getName() == arg)
				return star;
		}
		throw commandException.create(arg);
	}
}
