package me.hulipvp.hcf.game.faction.command;

import me.hulipvp.hcf.game.faction.command.args.player.all.*;
import me.hulipvp.hcf.game.faction.command.args.player.captain.*;
import me.hulipvp.hcf.game.faction.command.args.player.coleader.*;
import me.hulipvp.hcf.game.faction.command.args.player.leader.*;
import me.hulipvp.hcf.game.faction.command.args.player.member.*;
import me.hulipvp.hcf.game.faction.command.args.staff.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class FactionCommand {

    @Getter private final List<Object> arguments;

    public FactionCommand() {
        this.arguments = new ArrayList<>();

        this.arguments.add(new FactionAllyArgument());
        this.arguments.add(new FactionAnnouncementArgument());
        this.arguments.add(new FactionCaptainArgument());
        this.arguments.add(new FactionChatArgument());
        this.arguments.add(new FactionClaimArgument());
        this.arguments.add(new FactionCreateArgument());
        this.arguments.add(new FactionColeaderArugment());
        this.arguments.add(new FactionDepositArgument());
        this.arguments.add(new FactionDisbandArgument());
        this.arguments.add(new FactionFocusArgument());
        this.arguments.add(new FactionPlayerFocusCommand());
        this.arguments.add(new FactionHelpArgument());
        this.arguments.add(new FactionHomeArgument());
        this.arguments.add(new FactionInviteArgument());
        this.arguments.add(new FactionInvitesArgument());
        this.arguments.add(new FactionJoinArgument());
        this.arguments.add(new FactionKickArgument());
        this.arguments.add(new FactionLeaderArgument());
        this.arguments.add(new FactionLeaveArgument());
        this.arguments.add(new FactionListArgument());
        this.arguments.add(new FactionTopArgument());
        this.arguments.add(new FactionLivesArgument());
        this.arguments.add(new FactionLocationCommand());
        this.arguments.add(new FactionMapArgument());
        this.arguments.add(new FactionOpenArgument());
        this.arguments.add(new FactionSethomeArgument());
        this.arguments.add(new FactionShowArgument());
        this.arguments.add(new FactionRenameArgument());
        this.arguments.add(new FactionReviveArgument());
        this.arguments.add(new FactionStuckArgument());
        this.arguments.add(new FactionSubclaimArgument());
        this.arguments.add(new FactionUnallyArgument());
        this.arguments.add(new FactionUnclaimArgument());
        this.arguments.add(new FactionUninviteArgument());
        this.arguments.add(new FactionWithdrawArgument());
        this.arguments.add(new FactionRallyCommand());

        this.arguments.add(new FactionBypassArgument());
        this.arguments.add(new FactionClaimforArgument());
        this.arguments.add(new FactionCreateSystemArgument());
        this.arguments.add(new FactionForceDisbandArgument());
        this.arguments.add(new FactionForceJoinArgument());
        this.arguments.add(new FactionForceSethomeArgument());
        this.arguments.add(new FactionForceLeaderArgument());
        this.arguments.add(new FactionResetClaimsArgument());
        this.arguments.add(new FactionSetColorArgument());
        this.arguments.add(new FactionSetDeathbanArgument());
        this.arguments.add(new FactionSetPointsArgument());
        this.arguments.add(new FactionAddPointsArgument());
        this.arguments.add(new FactionSetDtrArgument());
        this.arguments.add(new FactionSetRegenArgument());
        this.arguments.add(new FactionTeleportArgument());
        this.arguments.add(new FactionTphereArgument());
    }
}
