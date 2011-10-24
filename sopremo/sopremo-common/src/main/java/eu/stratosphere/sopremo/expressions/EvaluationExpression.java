package eu.stratosphere.sopremo.expressions;

import java.util.Set;

import eu.stratosphere.sopremo.EvaluationContext;
import eu.stratosphere.sopremo.EvaluationException;
import eu.stratosphere.sopremo.ExpressionTag;
import eu.stratosphere.sopremo.jsondatamodel.JsonNode;
import eu.stratosphere.sopremo.jsondatamodel.NullNode;
import eu.stratosphere.util.IdentitySet;

public abstract class EvaluationExpression extends SopremoExpression<EvaluationContext, EvaluationExpression> {
	private static final class SameValueExpression extends EvaluationExpression {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6957283445639387461L;

		/**
		 * Returns the node without modifications.
		 */
		@Override
		public JsonNode evaluate(final JsonNode node, final EvaluationContext context) {
			return node;
		}

		private Object readResolve() {
			return EvaluationExpression.VALUE;
		}

		@Override
		public boolean equals(final Object obj) {
			return this == obj;
		}

		@Override
		public JsonNode set(final JsonNode node, final JsonNode value, final EvaluationContext context) {
			return value;
		}

		@Override
		protected void toString(final StringBuilder builder) {
			builder.append("<value>");
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(this);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1226647739750484403L;

	/**
	 * Used for secondary information during plan creation only.
	 */
	private transient Set<ExpressionTag> tags = new IdentitySet<ExpressionTag>();

	/**
	 * Sets the value of the node specified by this expression using the given {@link EvaluationContext}.
	 * 
	 * @param node
	 *        the node to change
	 * @param value
	 *        the value to set
	 * @param context
	 *        the current <code>EvaluationContext</code>
	 * @return the node or a new node if the expression directly accesses the node
	 */
	public JsonNode set(JsonNode node, JsonNode value, EvaluationContext context) {
		throw new UnsupportedOperationException(String.format(
			"Cannot change the value with expression %s of node %s to %s", this, node, value));
	}

	public final static EvaluationExpression KEY = new EvaluationExpression() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 9192628786637605317L;

		@Override
		public JsonNode evaluate(final JsonNode node, final EvaluationContext context) {
			throw new EvaluationException();
		}

		private Object readResolve() {
			return EvaluationExpression.KEY;
		}

		@Override
		protected void toString(final StringBuilder builder) {
			builder.append("<key>");
		};
	};

	public final static EvaluationExpression AS_KEY = new EvaluationExpression() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 9192628786637605317L;

		@Override
		public JsonNode evaluate(final JsonNode node, final EvaluationContext context) {
			throw new EvaluationException();
		}

		private Object readResolve() {
			return EvaluationExpression.AS_KEY;
		}

		@Override
		protected void toString(final StringBuilder builder) {
			builder.append("-><key>");
		};
	};

	/**
	 * Represents an expression that returns the input node without any modifications. The constant is mostly used for
	 * {@link Operator}s that do not perform any transformation to the input, such as a filter operator.
	 */
	public static final SameValueExpression VALUE = new SameValueExpression();

	public static final EvaluationExpression NULL = new ConstantExpression(NullNode.getInstance()) {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2375203649638430872L;

		private Object readResolve() {
			return EvaluationExpression.NULL;
		}
	};

	public void addTag(final ExpressionTag tag) {
		this.tags.add(tag);
	}

	public boolean hasTag(final ExpressionTag tag) {
		return this.tags.contains(tag);
	}

	public boolean removeTag(final ExpressionTag preserve) {
		return this.tags.remove(preserve);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		this.toString(builder);
		return builder.toString();
	}

	@Override
	protected void toString(StringBuilder builder) {
		if (!this.tags.isEmpty())
			builder.append(this.tags).append(" ");
		super.toString(builder);
	}

	public EvaluationExpression withTag(final ExpressionTag tag) {
		this.addTag(tag);
		return this;
	}
}
