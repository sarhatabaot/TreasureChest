package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class LevelReward implements IReward {
	
	private RewardInfo info;

	protected LevelReward(RewardInfo info) {
		this.info = info;
	}
	
	public LevelReward(int levels) {
		
		Map<String, Object> data = new HashMap<>();
		data.put("levels", levels);
		this.info = new RewardInfo("level", data);
	}

	@Override
	public RewardInfo getInfo() {
		return info;
	}
	
	public int getLevels() {
		return (Integer) info.getData("levels");
	}
	
	public void setLevels(int value) {
		info.setData("levels", value);
	}

	@Override
	public String getDescription() {
		return getLevels() + " levels";
	}

	@Override
	public void give(Player player) throws RewardException {
		player.setLevel(player.getLevel() + getLevels());
	}

}
